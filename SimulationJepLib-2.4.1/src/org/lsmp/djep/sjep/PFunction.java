/* @author rich
 * Created on 15-Dec-2004
 */
package org.lsmp.djep.sjep;

import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * Represents a function.
 * 
 * @author Rich Morris Created on 15-Dec-2004
 */
public class PFunction extends AbstractPNode {
	private String name;
	PostfixMathCommandI pfmc;
	PNodeI args[];

	/**
	 * 
	 */
	protected PFunction(PolynomialCreator pc, String name, PostfixMathCommandI pfmc, PNodeI args[]) {
		super(pc);
		this.name = name;
		this.pfmc = pfmc;
		this.args = args;
	}

	@Override
	public boolean equals(PNodeI node) {
		if (!(node instanceof PFunction))
			return false;
		PFunction fun = (PFunction) node;
		if (!name.equals(fun.name))
			return false;
		if (args.length != fun.args.length)
			return false;
		for (int i = 0; i < args.length; ++i)
			if (!args[i].equals(fun.args[i]))
				return false;
		return true;
	}

	/**
	 * this < arg ---> -1 this > arg ---> 1
	 */
	protected int compareTo(PFunction fun) {
		int res = name.compareTo(fun.name);
		if (res != 0)
			return res;

		if (args.length < fun.args.length)
			return -1;
		if (args.length > fun.args.length)
			return 1;

		for (int i = 0; i < args.length; ++i) {
			res = args[i].compareTo(fun.args[i]);
			if (res != 0)
				return res;
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append('(');
		for (int i = 0; i < args.length; ++i) {
			if (i > 0)
				sb.append(',');
			sb.append(args[i].toString());
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public Node toNode() throws ParseException {
		Node funargs[] = new Node[args.length];
		for (int i = 0; i < args.length; ++i)
			funargs[i] = args[i].toNode();
		Node fun = pc.nf.buildFunctionNode(name, pfmc, funargs);
		return fun;
	}

	@Override
	public PNodeI expand() throws ParseException {
		PNodeI newTerms[] = new PNodeI[args.length];
		for (int i = 0; i < args.length; ++i)
			newTerms[i] = args[i].expand();
		return new PFunction(pc, name, pfmc, newTerms);
	}
}
