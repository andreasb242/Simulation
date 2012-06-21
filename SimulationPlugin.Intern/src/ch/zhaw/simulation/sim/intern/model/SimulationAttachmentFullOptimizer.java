package ch.zhaw.simulation.sim.intern.model;

import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

import ch.zhaw.simulation.jep.functions.TimeDependent;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;

public class SimulationAttachmentFullOptimizer extends AbstractSimulationAttachmentOptimizer {
	private boolean timeDependent;
	private Object staticValue = null;

	private boolean DEBUG_OUTPUT = true;

	public SimulationAttachmentFullOptimizer(MatrixJep jep, String name) {
		super(jep, name);
	}

	@Override
	public void optimize(Node node, Vector<AssigmentPair> assigment) throws ParseException {
		super.optimize(node, assigment);

		Node processed = this.formula;

		this.timeDependent = isTimeDependent(processed);

		if (DEBUG_OUTPUT) {
			System.out.print("\n" + name + ": ");
			jep.getPrintVisitor().println(processed);
		}

		if (timeDependent) {
			formula = processed;

			if (DEBUG_OUTPUT) {
				System.out.println(name + ": is timeDependent");
			}
		} else {
			formula = jep.simplify(processed);
		}

		if (DEBUG_OUTPUT) {
			System.out.print("simplified: ");
			jep.getPrintVisitor().println(formula);
		}

		// don't depend on others and not time dependent => static
		if (assigment.size() == 0 && !timeDependent) {
			staticValue = jep.evaluate(formula);

			if (DEBUG_OUTPUT) {
				System.out.print("isStatic: ");
				jep.getPrintVisitor().println(formula);
			}
		} else {
			staticValue = null;
		}
	}

	/**
	 * 2. Optimierungsschritt: Wenn alle Variabeln const sind ist auch diese
	 * Const
	 * 
	 * @param assigment
	 */
	@Override
	public void optimizeForStatic(Vector<AssigmentPair> assigment) throws ParseException {
		if (staticValue != null) {
			return; // bereits ausgerechnet
		}

		if (this.timeDependent) {
			return;// do not optimize
		}

		// Testen ob alle abhängigen Objekte const sind
		for (AssigmentPair a : assigment) {
			if (a.isNeverStatic()) {
				return;
			}

			AbstractNamedSimulationData o = a.getSimulationObject();

			// container are usually not static, except if there are no flows...
			if (o instanceof SimulationContainerData) {
				return;
			}

			SimulationAttachment x = (SimulationAttachment) a.getSimulationObject().attachment;
			x.optimizeForStatic();
			if (x.getStaticValue() == null) {
				return;
			}
		}

		staticValue = jep.evaluate(formula);

		if (DEBUG_OUTPUT) {
			System.out.print(name + ": optimizeForStatic = ");
			jep.getPrintVisitor().println(formula);
		}
	}

	@Override
	public Object getStaticValue() {
		return staticValue;
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

}
