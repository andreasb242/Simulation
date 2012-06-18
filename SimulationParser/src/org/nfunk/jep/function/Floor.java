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
 * A PostfixMathCommandI which find the smallest integer below the number
 * ceil(pi) give 3 ceil(-i) give -4
 * 
 * @author Richard Morris
 * @see Math#floor(double)
 */

@Category(CategoryType.BASE)
@Example("(x)")
@Description("Find the smallest integer below the number. e.g. ceil(pi) give 3 ceil(-3.2) give -4")
public class Floor extends PostfixMathCommand {
	public Floor() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(floor(param));// push the result on the inStack
		return;
	}

	public Object floor(Object param) throws ParseException {
		if (param instanceof Number) {
			return new Double(Math.floor(((Number) param).doubleValue()));
		}

		throw new ParseException("Invalid parameter type");
	}

}
