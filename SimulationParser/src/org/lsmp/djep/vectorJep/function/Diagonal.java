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
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Creates a diagonal matrix, with a given vector as diagonals elements.
 * diag([1,2,3]) -> [[1,0,0],[0,2,0],[0,0,3]]
 * 
 * @author Rich Morris Created on 13-Feb-2005
 */
@Category(CategoryType.MATRIX)
@Example("(matrix)")
@Description("Creates a diagonal matrix, with a given vector as diagonals elements.<br>" +
		"diag([1,2,3]) -> [[1,0,0],[0,2,0],[0,0,3]]")
public class Diagonal extends PostfixMathCommand implements UnaryOperatorI {
	public Diagonal() {
		super();
		this.numberOfParameters = 1;
	}

	@Override
	public Dimensions calcDim(Dimensions ldim) {
		return Dimensions.valueOf(ldim.numEles(), ldim.numEles());
	}

	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs) throws ParseException {
		Matrix mat = (Matrix) res;
		int n = lhs.getNumEles();
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j)
				mat.setEle(i, j, new Double(0.0));
			mat.setEle(i, i, lhs.getEle(i));
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
