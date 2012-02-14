/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/

package org.nfunk.jep.function;

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.ParseException;

/**
 * The list function. Returns a Vector comprising all the children.
 * 
 * @author Rich Morris Created on 29-Feb-2004
 */
public class List extends PostfixMathCommand {
	public List() {
		numberOfParameters = -1;
	}

	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack); // check the stack
		if (curNumberOfParameters < 1)
			throw new ParseException("Empty list");
		Vector<Object> res = new Vector<Object>(curNumberOfParameters);
		res.setSize(curNumberOfParameters);
		for (int i = curNumberOfParameters - 1; i >= 0; --i) {
			Object param = inStack.pop();
			res.setElementAt(param, i);
		}
		inStack.push(res);
		return;
	}
}
