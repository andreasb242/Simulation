/* @author rich
 * Created on 01-Oct-2004
 */
package org.lsmp.djep.rewrite;

import org.lsmp.djep.xjep.XJep;
import org.lsmp.djep.xjep.XOperator;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.Operator;
import org.nfunk.jep.ParseException;

/**
 * @author Rich Morris Created on 01-Oct-2004
 */
public class ExpandBrackets extends AbstractRewrite {
	/**
	 * TODO_YEP cope with a * uminus(x+x)
	 */
	public ExpandBrackets(XJep xj) {
		super(xj);
	}

	@Override
	public boolean test(ASTFunNode node, Node[] children) {
		if (!node.isOperator())
			return false;
		XOperator op = (XOperator) node.getOperator();

		if (opSet.getMultiply() == op) {
			if (tu.getOperator(children[0]) == opSet.getAdd()) {
				return true;
			}
			if (tu.getOperator(children[0]) == opSet.getSubtract()) {
				return true;
			}
			if (tu.getOperator(children[1]) == opSet.getAdd()) {
				return true;
			}
			if (tu.getOperator(children[1]) == opSet.getSubtract()) {
				return true;
			}
		}
		return false;
	}

	public Node apply(ASTFunNode node, Node[] children) throws ParseException {

		Operator lhsOp = tu.getOperator(children[0]);
		Operator rhsOp = tu.getOperator(children[1]);

		// (a+b)*c --> (a*c)+(b*c)
		if (lhsOp == opSet.getAdd() || lhsOp == opSet.getSubtract()) {
			return nf.buildOperatorNode(lhsOp, nf.buildOperatorNode(opSet.getMultiply(), children[0].jjtGetChild(0), xj.deepCopy(children[1])),
					nf.buildOperatorNode(opSet.getMultiply(), children[0].jjtGetChild(1), xj.deepCopy(children[1])));

		}

		// a*(b+c) -> (a*b)+(a*c)
		if (rhsOp == opSet.getAdd() || rhsOp == opSet.getSubtract()) {
			return nf.buildOperatorNode(rhsOp, nf.buildOperatorNode(opSet.getMultiply(), xj.deepCopy(children[0]), children[1].jjtGetChild(0)),
					nf.buildOperatorNode(opSet.getMultiply(), xj.deepCopy(children[0]), children[1].jjtGetChild(1)));
		}
		throw new ParseException("ExpandBrackets at least one child must be + or -");
	}

}
