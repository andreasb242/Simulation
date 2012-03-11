package ch.zhaw.simulation.sim.intern.model;

import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.Add;
import org.nfunk.jep.function.Multiply;
import org.nfunk.jep.function.Subtract;

import ch.zhaw.simulation.math.Parser.ParserNodePair;
import ch.zhaw.simulation.math.VarPlaceholder;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.sim.intern.data.SimulationSerie;
import ch.zhaw.simulation.sim.intern.rungekutta.TmpValue;

public class SimulationAttachment implements ch.zhaw.simulation.model.SimulationAttachment {
	private Vector<AbstractNamedSimulationData> sources;
	private ParserNodePair parsed;
	private Vector<AssigmentPair> assigment = new Vector<AssigmentPair>();

	private Node formula = null;
	private Object value = null;

	private Object containerValue;

	public Object tmp;
	public SimulationSerie serie;

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

	public void assigneSourcesVars() {
		for (Node n : parsed.nodes) {
			checkAssignNodeTree(n);
		}

		// time
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
		if (containerValue != null) {
			if (tmp instanceof TmpValue) {
				Object v = ((TmpValue) tmp).getValue();
				if (v != null) {
					return v;
				}
			}
			return containerValue;
		}
		if (value != null) {
			return value;
		}
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

		formula = j.simplify(processed);

		if (assigment.size() == 0) {
			value = j.evaluate(formula);
			// String s = j.getPrintVisitor().formatValue(value);
			// System.out.println("Calculated value: " + s);
			// } else {
			// String s = j.getPrintVisitor().formatValue(formula);
			// System.out.println("Optimized formula: " + s);
		} else {
			value = null;
		}
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

		MatrixJep j = parsed.jep;

		// Testen ob alle abhängigen Objekte const sind
		for (AssigmentPair a : assigment) {
			if (a.isNeverStatic()) {
				// TODO: Container sind nur const wenn keine ein / ausflüse
				// vorhanden sind
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

	private void checkAssignNodeTree(Node node) {
		if (node instanceof ASTVarNode) {
			ASTVarNode a = (ASTVarNode) node;

			// TODO: optimieren
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

			assigment.add(new AssigmentPair((ASTVarNode) node, found));
		}

		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node n = node.jjtGetChild(i);
			checkAssignNodeTree(n);
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

		public AssigmentPair(ASTVarNode node, AbstractNamedSimulationData so) {
			this.node = node;
			this.so = so;
			this.neverStatic = so instanceof SimulationContainerData;

			if (so == null) {
				throw new NullPointerException("so == null");
			}
			if (node == null) {
				throw new NullPointerException("node == null");
			}
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
