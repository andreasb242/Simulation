/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package org.nfunk.jep.function;

import java.util.Stack;

import org.nfunk.jep.ParseException;

/**
 * Encapsulates the Math.random() function.
 */
public class Random extends PostfixMathCommand {
	public Random() {
		numberOfParameters = 0;
	}

	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		inStack.push(Math.random());
		return;
	}
}
