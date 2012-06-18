/* @author rich
 * Created on 13-Feb-2005
 *
 * See LICENSE.txt for license information.
 */
package org.lsmp.djep.vectorJep.function;

import java.util.Stack;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.Matrix;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.vectorJep.values.Tensor;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;

/**
 * Transpose a matrix. trans([[1,2],[3,4]]) -> [[1,3],[2,4]]
 * 
 * @author Rich Morris Created on 13-Feb-2005
 */
@Category(CategoryType.UNDEFINED)
public class Transpose extends PostfixMathCommand implements UnaryOperatorI {
	public Transpose() {
		super();
		this.numberOfParameters = 1;
	}

	@Override
	public Dimensions calcDim(Dimensions ldim) {
		return Dimensions.valueOf(ldim.getLastDim(), ldim.getFirstDim());
	}

	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs) throws ParseException {
		if (!(res instanceof Matrix))
			throw new ParseException("transpose: result must be a matrix");
		if (!(lhs instanceof Matrix))
			throw new ParseException("transpose: argument must be a matrix");
		Matrix resmat = (Matrix) res;
		Matrix mat = (Matrix) lhs;
		if (resmat.getNumCols() != mat.getNumRows() || resmat.getNumRows() != mat.getNumCols())
			throw new ParseException("transpose, dimension of result is wrong res " + resmat.getDim() + " arg " + mat.getDim());
		for (int i = 0; i < resmat.getNumRows(); ++i) {
			for (int j = 0; j < resmat.getNumCols(); ++j)
				resmat.setEle(i, j, mat.getEle(j, i));
		}
		return res;
	}

	@Override
	public void run(Stack<Object> s) throws ParseException {
		MatrixValueI obj = (MatrixValueI) s.pop();
		MatrixValueI res = Tensor.getInstance(calcDim(obj.getDim()));
		calcValue(res, obj);
		s.push(res);
	}
}
