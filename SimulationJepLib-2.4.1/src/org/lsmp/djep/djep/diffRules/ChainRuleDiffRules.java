/* @author rich
 * Created on 04-Jul-2003
 */
package org.lsmp.djep.djep.diffRules;

import org.lsmp.djep.djep.DJep;
import org.lsmp.djep.djep.DiffRulesI;
import org.lsmp.djep.xjep.*;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.*;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * Common methods used when the rules are specified by node trees or strings.
 * All subclasses use the chain rule to differentiate. df(g(x),h(x))/dx -> df/dg
 * * dg/dx + df/dh * dh/dx Variable names MUST be x,y for 1 to 2 variables or
 * x1,x2,x3 for 3 for more variables.
 */
abstract class ChainRuleDiffRules implements DiffRulesI {
	protected String name;
	protected PostfixMathCommandI pfmc;
	protected Node rules[] = null;

	/** Cannot construct outside the context of a differentation visitor. */
	public ChainRuleDiffRules() {
	}

	/** returns the name of the function */
	public String getName() {
		return name;
	}

	/** returns the PostfixMathCommandI for the function. */
	public PostfixMathCommandI getPfmc() {
		return pfmc;
	}

	/**
	 * Returns the number of rules which should be number of arguments of
	 * function
	 */
	public int getNumRules() {
		return rules.length;
	}

	/** returns the i-th rule as an expression tree. */
	public Node getRule(int i) {
		return rules[i];
	}

	/**
	 * Use the chain rule to differentiate. df(g(x),h(x))/dx -> df/dg * dg/dx +
	 * df/dh * dh/dx
	 */
	public Node differentiate(ASTFunNode node, String var, Node[] children, Node[] dchildren, DJep djep) throws ParseException {
		XOperatorSet opSet = (XOperatorSet) djep.getOperatorSet();
		NodeFactory nf = djep.getNodeFactory();
		FunctionTable funTab = djep.getFunctionTable();

		int nRules = rules.length;
		if (nRules != children.length)
			throw new ParseException("Error differentiating " + name + " number of rules " + nRules + " != number of arguments " + children.length);

		if (nRules == 1) {
			// df(g(x))/dx -> f'(g(x)) * g'(x)
			Node fprime = djep.deepCopy(rules[0]);
			Node g = children[0];
			Node fprimesub = djep.substitute(fprime, "x", g);
			Node gprime = dchildren[0];
			return nf.buildOperatorNode(opSet.getMultiply(), fprimesub, gprime);
		} else if (nRules == 2) {
			// df(g(x),h(x))/dx -> df/dg * dg/dx + df/dh * dh/dx
			Node df_dg = djep.deepCopy(rules[0]);
			Node df_dh = djep.deepCopy(rules[1]);
			Node gprime = dchildren[0];
			Node hprime = dchildren[1];
			df_dg = djep.substitute(df_dg, new String[] { "x", "y" }, children);
			df_dh = djep.substitute(df_dh, new String[] { "x", "y" }, children);

			return nf.buildOperatorNode(opSet.getAdd(), nf.buildOperatorNode(opSet.getMultiply(), df_dg, gprime), nf.buildOperatorNode(opSet.getMultiply(),
					df_dh, hprime));
		} else if (nRules < 1) {
			throw new ParseException("Error differentiating " + name + " zero differention rules!");
		} else {
			String names[] = new String[nRules];
			Node[] df_dg = new Node[nRules];
			Node[] products = new Node[nRules];
			for (int i = 0; i < nRules; ++i) {
				df_dg[i] = djep.deepCopy(rules[i]);
				names[i] = "x" + i;
			}
			for (int i = 0; i < nRules; ++i) {
				df_dg[i] = djep.substitute(df_dg[i], names, children);
				products[i] = nf.buildOperatorNode(opSet.getMultiply(), df_dg[i], dchildren[i]);
			}
			return nf.buildFunctionNode("sum", funTab.get("sum"), products);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name + "  \t");
		if (rules == null)
			sb.append("no diff rules possible parse error?");
		else
			for (int i = 0; i < getNumRules(); ++i) {
				sb.append("\t");
				// sb.append(dv.djep.toString(getRule(i)));
				// TODO print the rule.
			}
		return sb.toString();
	}
}
