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
@Description("The function atan() returns the arc tangent of x.")
public class ArcTangent extends PostfixMathCommand {
	public ArcTangent() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(atan(param));// push the result on the inStack
		return;
	}

	public Object atan(Object param) throws ParseException {
		if (param instanceof Complex) {
			return ((Complex) param).atan();
		} else if (param instanceof Number) {
			return new Double(Math.atan(((Number) param).doubleValue()));
		}

		throw new ParseException("Invalid parameter type");
	}
}
