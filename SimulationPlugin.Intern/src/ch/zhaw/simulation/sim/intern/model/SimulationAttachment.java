package ch.zhaw.simulation.sim.intern.model;

import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.Add;
import org.nfunk.jep.function.Multiply;
import org.nfunk.jep.function.PostfixMathCommandI;
import org.nfunk.jep.function.Subtract;

import ch.zhaw.simulation.jep.functions.TimeDependent;
import ch.zhaw.simulation.math.Parser.ParserNodePair;
import ch.zhaw.simulation.math.VarPlaceholder;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class SimulationAttachment implements ch.zhaw.simulation.model.SimulationAttachment {
	private Vector<AbstractNamedSimulationData> sources;
	private ParserNodePair parsed;
	private Vector<AssigmentPair> assigment = new Vector<AssigmentPair>();

	private Node formula = null;
	private Object value = null;

	private Object containerValue;

	public Object tmp;
	public SimulationSerie serie;
	private boolean timeDependent;

	public Object getContainerValue() {
		return containerValue;
	}

	public void setContainerValue(Object containerValue) {
		if (containerValue == null) {
			throw new NullPointerException("containerValue == null");
		}
		this.containerValue = containerValue;
	}

	public void addContainerValue(Object value, double dt) throws ParseException {
		if (value == null) {
			throw new NullPointerException("value == null");
		}

		value = multiple(value, dt);
		containerValue = add(containerValue, value);
	}

	public void minusContainerValue(Object value, double dt) throws ParseException {
		if (value == null) {
			throw new NullPointerException("value == null");
		}

		value = multiple(value, dt);

		containerValue = subtract(containerValue, value);
	}

	public Object add(Object param1, Object param2) throws ParseException {
		Add add = (Add) parsed.jep.getOperatorSet().getAdd().getPFMC();
		return add.add(param1, param2);
	}

	public Object subtract(Object param1, Object param2) throws ParseException {
		Subtract sub = (Subtract) parsed.jep.getOperatorSet().getSubtract().getPFMC();
		return sub.sub(param1, param2);
	}

	public Object multiple(Object param1, Object param2) throws ParseException {
		Multiply mul = (Multiply) parsed.jep.getOperatorSet().getMultiply().getPFMC();
		try {
			return mul.mul(param1, param2);
		} catch (ParseException e) {
			System.err.println("param1: " + param1);
			System.err.println("param2: " + param2);
			throw e;
		}
	}

	public void assigneSourcesVars(SimulationFlowModel flowModel) {
		for (Node n : parsed.nodes) {
			checkAssignNodeTree(n, flowModel);
		}
	}

	public void setParsed(ParserNodePair parsed) {
		if (parsed == null) {
			throw new NullPointerException("parsed == null");
		}
		this.parsed = parsed;
	}

	public ParserNodePair getParsed() {
		return parsed;
	}

	public void setSources(Vector<AbstractNamedSimulationData> sources) {
		if (sources == null) {
			throw new NullPointerException("sources == null");
		}
		this.sources = sources;
	}

	public Vector<AbstractNamedSimulationData> getSources() {
		return sources;
	}

	public Object calc(double time, double dt) throws ParseException {
		// if (containerValue != null) {
		// if (tmp instanceof TmpValue) {
		// Object v = ((TmpValue) tmp).getValue();
		// if (v != null) {
		// return v;
		// }
		// }
		// return containerValue;
		// }
		// if (value != null) {
		// return value;
		// }
		MatrixJep j = parsed.jep;

		j.setVarValue("time", time);
		j.setVarValue("dt", dt);

		assignVar(time, dt);

		return j.evaluate(formula);
	}

	public Object getValue() {
		return value;
	}

	private void assignVar(double time, double dt) throws ParseException {
		for (AssigmentPair a : assigment) {
			if (a instanceof TimeDtAssigmentPair) {
				continue;
			}
			Object o = ((SimulationAttachment) a.getSimulationObject().attachment).calc(time, dt);
			if (!a.getNode().getVar().setValue(o)) {
				throw new RuntimeException("Could not set variable");
			}
		}
	}

	public void optimize() throws ParseException {
		MatrixJep j = parsed.jep;
		Node processed = j.preprocess(parsed.nodes.lastElement());

		this.timeDependent = isTimeDependent(processed);

		if (timeDependent) {
			formula = processed;
		} else {
			formula = j.simplify(processed);
		}

//		j.getPrintVisitor().println(formula);

		if (assigment.size() == 0 && !timeDependent) {
			value = j.evaluate(formula);

			// System.out.print("DBG: ");
			// j.getPrintVisitor().println(formula);
		} else {
			value = null;
		}
	}

	private boolean isTimeDependent(Node node) {
		if (node instanceof ASTFunNode) {
			PostfixMathCommandI pfmc = ((ASTFunNode) node).getPFMC();
			if (pfmc != null && pfmc instanceof TimeDependent) {
				return true;
			}

			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				Node child = node.jjtGetChild(i);
				if (isTimeDependent(child)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 2. Optimierungsschritt: Wenn alle Variabeln const sind ist auch diese
	 * Const
	 * 
	 * @throws ParseException
	 */
	public void optimize2() throws ParseException {
		if (value != null) {
			return; // bereits ausgerechnet
		}
		
		if(this.timeDependent) {
			return;// do not optimize
		}

		MatrixJep j = parsed.jep;

		// Testen ob alle abhängigen Objekte const sind
		for (AssigmentPair a : assigment) {
			if (a.isNeverStatic()) {
				return;
			}

			SimulationAttachment x = (SimulationAttachment) a.getSimulationObject().attachment;
			x.optimize2();
			if (x.getValue() == null) {
				return;
			}
		}

		value = j.evaluate(formula);
	}

	private void checkAssignNodeTree(Node node, SimulationFlowModel flowModel) {
		if (node instanceof ASTVarNode) {
			ASTVarNode a = (ASTVarNode) node;

			if (!(a.getVar().getValue() instanceof VarPlaceholder)) {
				return;
			}

			String name = a.getName();

			AbstractNamedSimulationData found = null;

			if ("time".equals(name)) {
				assigment.add(new TimeDtAssigmentPair());
				return;
			}
			if ("dt".equals(name)) {
				assigment.add(new TimeDtAssigmentPair());
				return;
			}

			for (AbstractNamedSimulationData s : sources) {
				if (s.getName().equals(name)) {
					found = s;
					break;
				}
			}

			if (found == null) {
				throw new RuntimeException("Coulden't find representation for var: \"" + name + "\"");
			}

			boolean neverStatic;
			if (found instanceof SimulationContainerData) {
				neverStatic = true;

				Vector<FlowConnectorData> connectors = flowModel.getFlowConnectors();
				for (FlowConnectorData c : connectors) {
					if (c.getSource() == found || c.getTarget() == found) {
						neverStatic = false;
						break;
					}
				}
			} else {
				neverStatic = false;
			}

			assigment.add(new AssigmentPair((ASTVarNode) node, found, neverStatic));
		}

		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node n = node.jjtGetChild(i);
			checkAssignNodeTree(n, flowModel);
		}
	}

	private static class AssigmentPair {
		private ASTVarNode node;
		private AbstractNamedSimulationData so;

		private boolean neverStatic = false;

		protected AssigmentPair() {
		}

		public boolean isNeverStatic() {
			return neverStatic;
		}

		public AssigmentPair(ASTVarNode node, AbstractNamedSimulationData so, boolean neverStatic) {
			if (so == null) {
				throw new NullPointerException("so == null");
			}
			if (node == null) {
				throw new NullPointerException("node == null");
			}

			this.node = node;
			this.so = so;
			this.neverStatic = neverStatic;
		}

		public ASTVarNode getNode() {
			return node;
		}

		public AbstractNamedSimulationData getSimulationObject() {
			return so;
		}
	}

	private static class TimeDtAssigmentPair extends AssigmentPair {

		public TimeDtAssigmentPair() {
			super();
		}

		@Override
		public boolean isNeverStatic() {
			return true;
		}

		@Override
		public ASTVarNode getNode() {
			throw new RuntimeException("This method should not be called");
		}

		@Override
		public AbstractNamedSimulationData getSimulationObject() {
			throw new RuntimeException("This method should not be called");
		}
	}

	private Object nextFlowValue;

	public void setNextFlowValue(Object value) {
		nextFlowValue = value;
	}

	public Object getNextFlowValue() {
		return nextFlowValue;
	}

}
