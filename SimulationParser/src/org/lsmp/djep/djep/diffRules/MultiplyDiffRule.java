/* @author rich
 * Created on 04-Jul-2003
 */
package org.lsmp.djep.djep.diffRules;

import org.lsmp.djep.djep.DJep;
import org.lsmp.djep.djep.DiffRulesI;
import org.lsmp.djep.xjep.NodeFactory;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.Operator;
import org.nfunk.jep.OperatorSet;
import org.nfunk.jep.ParseException;

/**
 * Diffrentiates a product with respect to var. diff(y*z,x) ->
 * diff(y,x)*z+y*diff(z,x)
 * 
 * @since 28/1/05 now works when multiply has more than two arguments.
 */
public class MultiplyDiffRule implements DiffRulesI {
	private String name;

	public MultiplyDiffRule(String inName) {
		name = inName;
	}

	private Operator mulOp = null;

	public MultiplyDiffRule(String inName, Operator op) {
		name = inName;
		mulOp = op;
	}

	@Override
	public String toString() {
		return name + "  \t\tdiff(f*g,x) -> diff(f,x)*g+f*diff(g,x)";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Node differentiate(ASTFunNode node, String var, Node[] children, Node[] dchildren, DJep djep) throws ParseException {
		OperatorSet opset = djep.getOperatorSet();
		NodeFactory nf = djep.getNodeFactory();
		Operator op = opset.getMultiply();
		if (mulOp != null) {
			op = mulOp;
		}

		int nchild = node.jjtGetNumChildren();
		if (nchild == 2) {
			return nf.buildOperatorNode(opset.getAdd(), nf.buildOperatorNode(op, dchildren[0], djep.deepCopy(children[1])),
					nf.buildOperatorNode(op, djep.deepCopy(children[0]), dchildren[1]));
		}

		Node sums[] = new Node[nchild];
		for (int i = 0; i < nchild; ++i) {
			Node terms[] = new Node[nchild];
			for (int j = 0; j < nchild; ++j) {
				terms[j] = children[j];
			}
			terms[i] = dchildren[i];
			sums[i] = nf.buildOperatorNode(op, terms);
		}
		Node res = nf.buildOperatorNode(opset.getAdd(), sums);
		return res;
	}
}
