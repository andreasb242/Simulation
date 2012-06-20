/* @author rich
 * Created on 13-Feb-2005
 *
 * See LICENSE.txt for license information.
 */
package org.lsmp.djep.vectorJep.function;

import java.util.Stack;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MVector;
import org.lsmp.djep.vectorJep.values.Matrix;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.vectorJep.values.Tensor;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Extracts diagonal from a square matrix. getDiag([[1,0,0],[0,2,0],[0,0,3]]) ->
 * [1,2,3]
 * 
 * @author Rich Morris Created on 13-Feb-2005
 */
@Category(CategoryType.MATRIX)
@Example("(n×n Matrix)")
@Description("Extracts diagonal from a square matrix.<br>" +
		"getDiag([[1,0,0],[0,2,0],[0,0,3]]) -> [1,2,3]")
public class GetDiagonal extends PostfixMathCommand implements UnaryOperatorI {
	public GetDiagonal() {
		super();
		this.numberOfParameters = 1;
	}

	@Override
	public Dimensions calcDim(Dimensions ldim) {
		return Dimensions.valueOf(ldim.getFirstDim());
	}

	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs) throws ParseException {
		MVector vec = (MVector) res;
		Matrix mat = (Matrix) lhs;
		if (vec.getNumEles() != mat.getNumRows() || vec.getNumEles() != mat.getNumCols())
			throw new ParseException("getDiag requires a square matrix");
		for (int i = 0; i < vec.getNumEles(); ++i) {
			vec.setEle(i, mat.getEle(i, i));
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
