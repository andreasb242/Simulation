/* @author rich
 * Created on 13-Feb-2005
 *
 * See LICENSE.txt for license information.
 */
package org.lsmp.djep.vectorJep.function;

import java.util.Stack;

import org.lsmp.djep.vectorJep.values.Matrix;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;

/**
 * Creates an identity matrix. id(3) -> [[1,0,0],[0,1,0],[0,0,1]]
 * 
 * @author Rich Morris Created on 13-Feb-2005
 */
@Category(CategoryType.UNDEFINED)
public class Id extends PostfixMathCommand {
	public Id() {
		this.numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> s) throws ParseException {
		Object obj = s.pop();
		int n = ((Number) obj).intValue();
		Matrix mat = (Matrix) Matrix.getInstance(n, n);
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j)
				mat.setEle(i, j, new Double(0.0));
			mat.setEle(i, i, new Double(1.0));
		}
		s.push(mat);
	}
}
