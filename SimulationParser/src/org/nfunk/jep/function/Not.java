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

@Category(CategoryType.LOGICAL)
public class Not extends PostfixMathCommand {
	public Not() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		if (param instanceof Number) {
			int r = (((Number) param).doubleValue() == 0) ? 1 : 0;
			inStack.push(new Double(r));// push the result on the inStack
		} else if (param instanceof Boolean) {
			int r = (((Boolean) param).booleanValue()) ? 0 : 1;
			inStack.push(new Double(r));// push the result on the inStack
		} else {
			throw new ParseException("Invalid parameter type");
		}
	}

}
