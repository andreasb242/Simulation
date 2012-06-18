/* @author rich
 * Created on 04-Jul-2003
 */
package org.lsmp.djep.djep;

import java.util.Stack;

import org.lsmp.djep.xjep.CommandVisitorI;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;

/**
 * The diff(f,x) operator.
 */
@Category(CategoryType.UNDEFINED)
public class Diff extends PostfixMathCommand implements CommandVisitorI {
	public Diff() {
		super();
		super.numberOfParameters = 2;
	}

	/**
	 * Should never be evaluated!
	 * 
	 * @throws ParseException
	 *             if called by evaluator.
	 */
	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		throw new ParseException("Cannot evaluate the diff function. ");
	}

	/**
	 * Process the differentiation specified by node. Defines process in
	 * 
	 * @see CommandVisitorI
	 */
	@Override
	public Node process(Node node, Node children[], XJep xjep) throws ParseException {
		Node lhs = children[0];
		Node rhs = children[1];
		if (!xjep.getTreeUtils().isVariable(rhs)) {
			throw new ParseException("Format should be diff(f,x) where x is a variables and 1,2 are constants");
		}
		ASTVarNode var;
		try {
			// lhs = (Node) node.jjtGetChild(0);
			var = (ASTVarNode) rhs;
		} catch (ClassCastException e) {
			throw new ParseException("Format should be diff(f,x) where x is a variables and 1,2 are constants");
		}

		return ((DJep) xjep).differentiate(lhs, var.getName());
	}
}
