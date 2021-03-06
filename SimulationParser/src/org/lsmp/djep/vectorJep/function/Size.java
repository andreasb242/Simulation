/* @author rich
 * Created on 13-Feb-2005
 *
 * See LICENSE.txt for license information.
 */
package org.lsmp.djep.vectorJep.function;

import java.util.Stack;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MVector;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.vectorJep.values.Scaler;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Returns the size of an Scaler, Vector or Matrix.
 * 
 * <pre>
 * size(7) -> 1
 * size([1,2,3]) -> 3
 * size([[1,2,3],[4,5,6]]) -> [2,3]
 * size([[[1,2],[3,4],[5,6]],[[7,8],[9,10],[11,12]]]) -> [2,3,2]
 * </pre>
 * 
 * @author Rich Morris Created on 13-Feb-2005
 */
@Category(CategoryType.MATRIX)
@Example("(x)")
@Description("Returns the size of an Scaler, Vector or Matrix.<br><br>"+
	"size(7) -> 1<br>"+
	"size([1,2,3]) -> 3<br>"+
	"size([[1,2,3],[4,5,6]]) -> [2,3]<br>"+
	"size([[[1,2],[3,4],[5,6]],[[7,8],[9,10],[11,12]]]) -> [2,3,2]<br>")
public class Size extends PostfixMathCommand implements UnaryOperatorI {
	public Size() {
		super();
		this.numberOfParameters = 1;
	}

	@Override
	public Dimensions calcDim(Dimensions ldim) {
		int rank = ldim.rank();
		if (rank == 0)
			return Dimensions.ONE;
		return Dimensions.valueOf(rank);
	}

	@Override
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs) throws ParseException {
		Dimensions dims = lhs.getDim();
		if (dims.is0D()) {
			res.setEle(0, 1);
			return res;
		}
		for (int i = 0; i < dims.rank(); ++i) {
			res.setEle(i, dims.getIthDim(i));
		}
		return res;
	}

	@Override
	public void run(Stack<Object> s) throws ParseException {
		Object obj = s.pop();
		MatrixValueI res = null;
		if (obj instanceof Scaler) {
			res = Scaler.getInstance(new Integer(1));
		} else if (obj instanceof MVector)
			res = Scaler.getInstance(new Integer(((MVector) obj).getNumEles()));
		else if (obj instanceof MatrixValueI) {
			Dimensions inDim = ((MatrixValueI) obj).getDim();
			res = MVector.getInstance(inDim.rank());
			for (int i = 0; i < inDim.rank(); ++i)
				res.setEle(i, new Integer(inDim.getIthDim(i)));
		} else
			res = Scaler.getInstance(1);
		s.push(res);
	}
}
