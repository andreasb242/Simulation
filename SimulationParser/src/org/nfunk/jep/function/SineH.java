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

@Category(CategoryType.TRIGONOMETRIC)
@Example("(x)")
@Description("The function sinh() returns the hyperbolic sine of x.")
public class SineH extends PostfixMathCommand {
	public SineH() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(sinh(param));// push the result on the inStack
		return;
	}

	public Object sinh(Object param) throws ParseException {
		if (param instanceof Complex) {
			return ((Complex) param).sinh();
		} else if (param instanceof Number) {
			double value = ((Number) param).doubleValue();
			return new Double((Math.exp(value) - Math.exp(-value)) / 2);
		}

		throw new ParseException("Invalid parameter type");
	}

}
