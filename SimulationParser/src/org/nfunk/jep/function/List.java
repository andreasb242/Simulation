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

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.functions.NonOptimizeable;

/**
 * The list function. Returns a Vector comprising all the children.
 * 
 * NonOptimizeable: prevent lists of constants being converted what happens is
 * that for [[1,2],[3,4]] the dimension is not passed into buildConstantNode so
 * list is treated as [1,2,3,4] Ideally there would be a special simplification
 * rule for List
 * 
 * @author Rich Morris Created on 29-Feb-2004
 */
@Category(CategoryType.UNDEFINED)
public class List extends PostfixMathCommand implements NonOptimizeable {
	public List() {
		numberOfParameters = -1;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack); // check the stack
		if (curNumberOfParameters < 1) {
			throw new ParseException("Empty list");
		}

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
