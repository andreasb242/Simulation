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

@Category(CategoryType.UNDEFINED)
public class Cross extends PostfixMathCommand {
	private static Subtract sub = new Subtract();
	private static Multiply mul = new Multiply();

	public Cross() {
		numberOfParameters = 2;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack); // check the stack

		Object param2 = inStack.pop();
		Object param1 = inStack.pop();

		inStack.push(cross(param1, param2));

		return;
	}

	@SuppressWarnings("unchecked")
	public Object cross(Object param1, Object param2) throws ParseException {
		if (param1 instanceof Vector<?> && param2 instanceof Vector<?>) {
			return cross((Vector<Object>) param1, (Vector<Object>) param2);
		}
		throw new ParseException("Cross: Invalid parameter type, both arguments must be vectors");
	}

	public Object cross(Vector<Object> lhs, Vector<Object> rhs) throws ParseException {
		int len = lhs.size();
		if ((len != 2 && len != 3) || len != rhs.size())
			throw new ParseException("Cross: both sides must be of length 3");
		if (len == 3) {
			Vector<Object> res = new Vector<Object>(3);
			res.setSize(3);
			res.setElementAt(sub.sub(mul.mul(lhs.elementAt(1), rhs.elementAt(2)), mul.mul(lhs.elementAt(2), rhs.elementAt(1))), 0);
			res.setElementAt(sub.sub(mul.mul(lhs.elementAt(2), rhs.elementAt(0)), mul.mul(lhs.elementAt(0), rhs.elementAt(2))), 1);
			res.setElementAt(sub.sub(mul.mul(lhs.elementAt(0), rhs.elementAt(1)), mul.mul(lhs.elementAt(1), rhs.elementAt(0))), 2);
			return res;
		} else {
			return sub.sub(mul.mul(lhs.elementAt(0), rhs.elementAt(1)), mul.mul(lhs.elementAt(1), rhs.elementAt(0)));

		}
	}
}
