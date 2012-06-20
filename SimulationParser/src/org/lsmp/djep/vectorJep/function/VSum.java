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
import org.nfunk.jep.function.Add;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Adds the elements of a vector or matrix. vsum([1,2,3]) -> 6
 * vsum([[1,2],[3,4]]) -> 10
 * 
 * @author Rich Morris Created on 13-Feb-2005
 */
@Category(CategoryType.MATRIX)
@Example("(matrix)")
@Description("Adds the elements of a vector or matrix. vsum([1,2,3]) -> 6<br>" +
		"vsum([[1,2],[3,4]]) -> 10")
public class VSum extends PostfixMathCommand implements UnaryOperatorI {
	private Add add = new Add();

	public VSum() {
		super();
		this.numberOfParameters = 1;
	}

	@Override
	public Dimensions calcDim(Dimensions ldim) {
		return Dimensions.ONE;
	}

	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs) throws ParseException {
		if (!(res instanceof Scaler))
			throw new ParseException("vsum: result must be a scaler");

		Object val = lhs.getEle(0);
		for (int i = 1; i < lhs.getNumEles(); ++i)
			val = add.add(val, lhs.getEle(i));
		res.setEle(0, val);

		return res;
	}

	@Override
	public void run(Stack<Object> s) throws ParseException {
		MatrixValueI obj = (MatrixValueI) s.pop();
		MatrixValueI res = Scaler.getInstance(new Double(0.0));
		calcValue(res, obj);
		s.push(res);
	}
}
