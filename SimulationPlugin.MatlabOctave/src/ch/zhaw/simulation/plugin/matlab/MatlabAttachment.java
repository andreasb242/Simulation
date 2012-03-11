package ch.zhaw.simulation.plugin.matlab;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.Parser.ParserNodePair;
import ch.zhaw.simulation.model.SimulationAttachment;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;

public class MatlabAttachment implements SimulationAttachment {
	private Vector<AbstractNamedSimulationData> sources;
	private ParserNodePair parsed;
	private Vector<AssigmentPair> assigment = new Vector<AssigmentPair>();
	private Node formula = null;
	private Object value = null;

	/**
	 * Dependency Order: 0 no dependency, 1: depends on 0, 2: depends on 0 and 1
	 * etc.
	 * 
	 * -1 means not yet calculated
	 */
	private int dependencyOrder = -1;

	/**
	 * The constant value if any
	 */
	private double constValue;

	/**
	 * If this Object is const, we use <code>constValue</code> instead of
	 * calculate the value every time
	 */
	private boolean isConst = false;

	public MatlabAttachment() {
		super();
	}

	public void setSources(Vector<AbstractNamedSimulationData> sources) {
		if (sources == null) {
			throw new NullPointerException("sources == null");
		}
		this.sources = sources;
	}

	/**
	 * Set Attachment to "parsed" (wurde gerade geparst) with a ParserNodePair object
	 *
	 * @param parsed
	 */
	public void setParsed(ParserNodePair parsed) {
		// TODO: stimmt kommentar? oder ist methoden-name semi-optimal gewählt?
		if (parsed == null) {
			throw new NullPointerException("parsed == null");
		}
		this.parsed = parsed;
	}

	public ParserNodePair getParsed() {
		return parsed;
	}

	public void assigneSourcesVars() throws VarNotFoundExceptionTmp {
		for (Node n : parsed.nodes) {
			checkAssignNodeTree(n);
		}
	}

	/**
	 * 2. Optimierungsschritt: Wenn alle Variabeln const sind ist auch diese
	 * Const
	 * 
	 * @throws ParseException
	 */
	public void optimizeStatic(SimulationFlowModel model) throws ParseException {
		if (value != null) {
			return; // bereits ausgerechnet
		}

		MatrixJep j = parsed.jep;

		// Testen ob alle abhängigen Objekte const sind
		for (AssigmentPair a : assigment) {
			if (a.isSysvar()) {
				return;
			}

			if (a.getSimulationObject() instanceof SimulationContainerData) {
				if (model.hasFlowConnectors((SimulationContainerData) a.getSimulationObject())) {
					return;
				}
			}

			MatlabAttachment x = (MatlabAttachment) a.getSimulationObject().attachment;
			x.optimizeStatic(model);
			if (x.getValue() == null) {
				return;
			}
		}

		value = j.evaluate(formula);
	}

	// TODO: nicht schon gemacht bei Parser.processEquation() ?
	public void optimize() throws ParseException {
		MatrixJep j = parsed.jep;
		Node processed = j.preprocess(parsed.nodes.lastElement());

		formula = j.simplify(processed);

		if (assigment.size() == 0) {
			value = j.evaluate(formula);
			// String s = j.getPrintVisitor().formatValue(value);
			// System.out.println("Calculated value: " + s);
		} else {
			// String s = j.getPrintVisitor().formatValue(formula);
			// System.out.println("Optimized formula: " + s);
			value = null;
		}
	}

	public int calcOrder() {
		if (dependencyOrder != -1) {
			// already calculated
			return dependencyOrder;
		}

		if (assigment.size() == 0) {
			dependencyOrder = 0;
			return dependencyOrder;
		}

		int x = 0;
		for (AssigmentPair ap : assigment) {
			if (ap.isSysvar()) {
				continue;
			}

			MatlabAttachment a = (MatlabAttachment) ap.getSimulationObject().attachment;
			x = Math.max(x, a.calcOrder());
		}

		dependencyOrder = x + 1;

		return dependencyOrder;
	}

	public int getDependencyOrder() {
		return dependencyOrder;
	}

	private void checkAssignNodeTree(Node node) throws VarNotFoundExceptionTmp {
		if (node instanceof ASTVarNode) {
			ASTVarNode a = (ASTVarNode) node;

			if (!(a.getVar().getValue() instanceof Parser.VarPlaceholder)) {
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
				throw new VarNotFoundExceptionTmp(name);
			}

			assigment.add(new AssigmentPair((ASTVarNode) node, found));
		}

		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node n = node.jjtGetChild(i);
			checkAssignNodeTree(n);
		}
	}

	public Object getValue() {
		return value;
	}

	public boolean isConst() {
		return isConst;
	}

	public void setConstValue(double constValue) {
		this.constValue = constValue;
		this.isConst = true;
	}

	public void setNotConst() {
		this.isConst = false;
	}

	public double getConstValue() {
		return constValue;
	}

	public String getPreparedFormula(PrintVisitor visitor) {
		if (this.formula == null) {
			throw new NullPointerException();
		}

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream s = new PrintStream(bo);
		visitor.print((Node) this.formula, s);
		s.close();
		return bo.toString();
	}

	private static class AssigmentPair {
		private ASTVarNode node;
		private AbstractNamedSimulationData so;

		protected AssigmentPair() {
		}

		public boolean isSysvar() {
			return false;
		}

		public AssigmentPair(ASTVarNode node, AbstractNamedSimulationData so) {
			this.node = node;
			this.so = so;

			if (so == null) {
				throw new NullPointerException("so == null");
			}
			if (node == null) {
				throw new NullPointerException("node == null");
			}
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
		public boolean isSysvar() {
			return true;
		}

		@Override
		public AbstractNamedSimulationData getSimulationObject() {
			throw new RuntimeException("This method should not be called");
		}
	}

	public static class VarNotFoundExceptionTmp extends Exception {
		private static final long serialVersionUID = 1L;

		public VarNotFoundExceptionTmp(String var) {
			super(var);
		}
	}
}
