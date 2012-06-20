/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package org.nfunk.jep.function;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Encapsulates the Math.random() function.
 * 
 * TODO Unittests
 * 
 */
@Category(CategoryType.DISCRET)
@Example("()")
@Description("Withouth parameter: random number between 0 and 1<br>with Arguments (a, b) random number between a and b")
public class Random extends PostfixMathCommand {
	private Multiply mul = new Multiply();
	private Subtract sub = new Subtract();
	private Add add = new Add();

	public Random() {
		numberOfParameters = -1;
	}

	@Override
	public void run(Stack<Object> stack) throws ParseException {
		checkStack(stack);// check the stack

		if (curNumberOfParameters != 0 && curNumberOfParameters != 2) {
			throw new ParseException("rand expects 0 or two arguments!");
		}

		if (curNumberOfParameters == 0) {
			stack.push(Math.random());
		} else {
			Object a = stack.pop();
			Object b = stack.pop();

			stack.push(mul.mul(Math.random(), add.add(sub.sub(b, a), a)));
		}
	}
}
