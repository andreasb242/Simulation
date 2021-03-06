/* @author rich
 * Created on 27-Jul-2003
 */
package org.lsmp.djep.vectorJep.function;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MVector;
import org.lsmp.djep.vectorJep.values.Matrix;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.vectorJep.values.Tensor;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.UMinus;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;

/**
 * Unitary minus for matrices.
 * 
 * @author Rich Morris Created on 27-Jul-2003
 */
@Category(CategoryType.UNDEFINED)
public class MUMinus extends UMinus implements UnaryOperatorI {
	public MUMinus() {
	}

	@Override
	public Dimensions calcDim(Dimensions ldim) {
		return ldim;
	}

	/**
	 * calculates the value.
	 * 
	 * @param res
	 *            - results will be stored in this object
	 * @param lhs
	 *            - the value to be negated
	 * @return res
	 */
	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs) throws ParseException {
		int len = res.getNumEles();
		for (int i = 0; i < len; ++i)
			res.setEle(i, super.umin(lhs.getEle(i)));
		return res;
	}

	/**
	 * Negate an objects.
	 */
	@Override
	public Object umin(Object param1) throws ParseException {
		if (param1 instanceof MVector)
			return umin((MVector) param1);
		if (param1 instanceof Matrix)
			return umin((Matrix) param1);
		return super.umin(param1);
	}

	/** negate a vector. */
	private MVector umin(MVector lhs) throws ParseException {
		MVector res = new MVector(lhs.getNumEles());
		return (MVector) calcValue(res, lhs);
	}

	/** negate a matrix. */
	private Matrix umin(Matrix lhs) throws ParseException {
		Matrix res = (Matrix) Matrix.getInstance(lhs.getNumRows(), lhs.getNumCols());
		return (Matrix) calcValue(res, lhs);
	}

	/** negate a tensor. */
	public Tensor umin(Tensor lhs) throws ParseException {
		Tensor res = new Tensor(lhs);
		return (Tensor) calcValue(res, lhs);
	}
}
