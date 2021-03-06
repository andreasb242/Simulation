/* @author rich
 * Created on 13-Feb-2005
 *
 * See LICENSE.txt for license information.
 */
package org.lsmp.djep.vectorJep.function;

import java.util.Stack;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.vectorJep.values.Scaler;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Returns the length of a vector. If the argument is a scaler, matrix or tensor
 * returns total number of elements.
 * 
 * <pre>
 * len(5) -> 1
 * len([1,2,3]) -> 3
 * len([[1,2],[3,4],[5,6]]) -> 6
 * </pre>
 * 
 * @author Rich Morris Created on 13-Feb-2005
 */
@Category(CategoryType.MATRIX)
@Example("(x)")
@Description("Returns the length of a vector. If the argument is a scaler, matrix or tensor returns total number of elements.<br>" +
		"len(5) -> 1<br>" +
		"len([1,2,3]) -> 3<br>" +
		"len([[1,2],[3,4],[5,6]]) -> 6")
public class Length extends PostfixMathCommand implements UnaryOperatorI {
	public Length() {
		super();
		this.numberOfParameters = 1;
	}

	@Override
	public Dimensions calcDim(Dimensions ldim) {
		return Dimensions.ONE;
	}

	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs) throws ParseException {
		int neles = lhs.getNumEles();
		res.setEle(0, neles);
		return res;
	}

	@Override
	public void run(Stack<Object> aStack) throws ParseException {
		Object obj = aStack.pop();
		MatrixValueI res = null;
		if (obj instanceof MatrixValueI) {
			res = Scaler.getInstance(((MatrixValueI) obj).getNumEles());
		} else
			res = Scaler.getInstance(1);
		aStack.push(res);
	}
}
