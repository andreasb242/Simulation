package ch.zhaw.simulation.sim.intern.model;

import java.util.Vector;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class AbstractSimulationAttachmentOptimizer {
	protected MatrixJep jep;

	protected Node formula = null;

	protected final String name;

	public AbstractSimulationAttachmentOptimizer(MatrixJep jep, String name) {
		this.jep = jep;
		this.name = name;
	}

	public void optimize(Node node, Vector<AssigmentPair> assigment) throws ParseException {
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
