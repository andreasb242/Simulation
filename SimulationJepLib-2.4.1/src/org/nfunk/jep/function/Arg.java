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

/**
 * Argument of a complex number
 * 
 * @author Rich Morris Created on 20-Nov-2003
 */
@Category(CategoryType.UNDEFINED)
public class Arg extends PostfixMathCommand {
	private static final Double ONE = new Double(1.0);

	public Arg() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(arg(param));// push the result on the inStack
		return;
	}

	public Number arg(Object param) throws ParseException {
		if (param instanceof Complex) {
			return new Double(((Complex) param).arg());
		} else if (param instanceof Number) {
			return ONE;
		}
		throw new ParseException("Invalid parameter type");
	}
}
