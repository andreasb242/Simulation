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
 * Converts an object into its string representation. Calls the toString method
 * of the object.
 * 
 * @author Rich Morris Created on 27-Mar-2004
 */
@Category(CategoryType.BASE)
@Example("(x)")
@Description("Converts an object into its string representation")
public class Str extends PostfixMathCommand {
	public Str() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(param.toString());// push the result on the inStack
		return;
	}
}
