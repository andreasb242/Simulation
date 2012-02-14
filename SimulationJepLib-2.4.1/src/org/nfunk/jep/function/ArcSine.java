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

public class ArcSine extends PostfixMathCommand {
	public ArcSine() {
		numberOfParameters = 1;
	}

	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(asin(param));// push the result on the inStack
		return;
	}

	public Object asin(Object param) throws ParseException {
		if (param instanceof Complex) {
			return ((Complex) param).asin();
		} else if (param instanceof Number) {
			return new Double(Math.asin(((Number) param).doubleValue()));
		}

		throw new ParseException("Invalid parameter type");
	}
}
