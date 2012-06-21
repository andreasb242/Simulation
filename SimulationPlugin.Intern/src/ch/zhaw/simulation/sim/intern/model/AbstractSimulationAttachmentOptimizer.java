package ch.zhaw.simulation.sim.intern.model;

import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class AbstractSimulationAttachmentOptimizer {
	protected MatrixJep jep;

	protected Node formula = null;

	public AbstractSimulationAttachmentOptimizer(MatrixJep jep) {
		this.jep = jep;
	}

	public void optimize(Node node) throws ParseException {
		this.formula = jep.preprocess(node);
	}

	public Node getFormula() {
		return formula;
	}

	/**
	 * Calculate all static variables, because they don't need to be calculated
	 * each time
	 */
	public void optimizeForStatic(Vector<AssigmentPair> assigment) throws ParseException {
	}

	/**
	 * @return The static, optimized value. Or <code>null</code> if not static
	 */
	public Object getStaticValue() {
		return null;
	}
}
