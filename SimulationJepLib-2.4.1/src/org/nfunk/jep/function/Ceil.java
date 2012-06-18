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
 * A PostfixMathCommandI which find the smallest integer above the number
 * ceil(pi) give 4 ceil(-i) give -3
 * 
 * @author Richard Morris
 * @see Math#ceil(double)
 */
@Category(CategoryType.BASE)
@Example("(x)")
@Description("Finds the smallest integer above the number, e.g. ceil(pi) give 4 ceil(-3.2) give -3")
public class Ceil extends PostfixMathCommand {
	public Ceil() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(ceil(param));// push the result on the inStack
		return;
	}

	public Object ceil(Object param) throws ParseException {
		if (param instanceof Number) {
			return new Double(Math.ceil(((Number) param).doubleValue()));
		}

		throw new ParseException("Invalid parameter type");
	}

}
