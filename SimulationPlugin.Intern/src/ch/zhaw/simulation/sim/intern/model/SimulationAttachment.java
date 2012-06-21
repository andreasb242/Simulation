package ch.zhaw.simulation.sim.intern.model;

import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.math.Parser.ParserNodePair;
import ch.zhaw.simulation.math.VarPlaceholder;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class SimulationAttachment implements ch.zhaw.simulation.model.SimulationAttachment {
	private Vector<AbstractNamedSimulationData> sources;
	protected ParserNodePair parsed;
	private Vector<AssigmentPair> assigment = new Vector<AssigmentPair>();

	public Object tmp;
	public SimulationSerie serie;

	private AbstractSimulationAttachmentOptimizer optimizer;

	/**
	 * Enable / Disable optimizer for testing reasons
	 */
	private static final boolean OPTIMIZE = false;

	public SimulationAttachment() {
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

		if (OPTIMIZE) {
			this.optimizer = new SimulationAttachmentFullOptimizer(parsed.jep);
		} else {
			this.optimizer = new SimulationAttachmentNoOptimizer(parsed.jep);
		}
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
		if (getStaticValue() != null) {
			return getStaticValue();
		}
		MatrixJep j = parsed.jep;

		j.setVarValue("time", time);
		j.setVarValue("dt", dt);

		assignVar(time, dt);

		return j.evaluate(this.optimizer.getFormula());
	}

	public Object getStaticValue() {
		return this.optimizer.getStaticValue();
	}

	/**
	 * Calculate all depending on values
	 * 
	 * TODO Optimize, this should no be neccessary, because parameter should be
	 * calculated only once!
	 * 
	 * @param time
	 * @param dt
	 * @throws ParseException
	 */
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
		this.optimizer.optimize(parsed.nodes.lastElement());
	}

	/**
	 * 2. Optimierungsschritt: Wenn alle Variabeln const sind ist auch diese
	 * Const
	 * 
	 * @throws ParseException
	 */
	public void optimizeForStatic() throws ParseException {
		this.optimizer.optimizeForStatic(assigment);
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
}
