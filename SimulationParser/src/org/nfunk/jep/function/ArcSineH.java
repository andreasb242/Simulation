/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package org.nfunk.jep.function;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Complex;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Implements the arcSinH function.
 * 
 * @author Nathan Funk
 * @since 2.3.0 beta 2 - Now returns Double result rather than Complex for
 *        Double arguments.
 */
@Category(CategoryType.TRIGONOMETRIC)
@Example("(x)")
@Description("The inverse hyperbolic sine for x.")
public class ArcSineH extends PostfixMathCommand {
	public ArcSineH() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(asinh(param));// push the result on the inStack
		return;
	}

	public Object asinh(Object param) throws ParseException {
		if (param instanceof Complex) {
			return ((Complex) param).asinh();
		} else if (param instanceof Number) {
			double val = ((Number) param).doubleValue();
			double res = Math.log(val + Math.sqrt(val * val + 1));
			return new Double(res);
		}
		throw new ParseException("Invalid parameter type");
	}
}
