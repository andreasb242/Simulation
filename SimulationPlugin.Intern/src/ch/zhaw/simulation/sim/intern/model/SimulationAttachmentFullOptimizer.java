package ch.zhaw.simulation.sim.intern.model;

import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

import ch.zhaw.simulation.jep.functions.TimeDependent;

public class SimulationAttachmentFullOptimizer extends AbstractSimulationAttachmentOptimizer {
	private boolean timeDependent;
	private Object staticValue = null;

	public SimulationAttachmentFullOptimizer(MatrixJep jep) {
		super(jep);
	}

	@Override
	public void optimize(Node node) throws ParseException {
		super.optimize(node);

		Node processed = this.formula;

		this.timeDependent = isTimeDependent(processed);

		// if (timeDependent) {
		// formula = processed;
		// } else {
		// formula = jep.simplify(processed);
		// }
		//
		// jep.getPrintVisitor().println(formula);
		//
		// if (assigment.size() == 0 && !timeDependent) {
		// value = jep.evaluate(formula);
		//
		// System.out.print("DBG: ");
		// jep.getPrintVisitor().println(formula);
		// } else {
		// value = null;
		// }
		// } else {
		// jep.getPrintVisitor().println(processed);
		// formula = processed;
		// }
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

			SimulationAttachment x = (SimulationAttachment) a.getSimulationObject().attachment;
			x.optimizeForStatic();
			if (x.getStaticValue() == null) {
				return;
			}
		}

		staticValue = jep.evaluate(formula);
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
