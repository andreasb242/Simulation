/* @author rich
 * Created on 14-Dec-2004
 */
package org.lsmp.djep.sjep;

import org.lsmp.djep.xjep.XVariable;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * Represents a variable.
 * 
 * @author Rich Morris Created on 14-Dec-2004
 */
public class PVariable extends AbstractPNode {
	private XVariable variable;

	protected PVariable(PolynomialCreator pc, XVariable var) {
		super(pc);
		this.variable = var;
	}

	@Override
	public boolean equals(PNodeI node) {
		if (node instanceof PVariable)
			if (variable.equals(((PVariable) node).variable))
				return true;

		return false;
	}

	/**
	 * this < arg ---> -1 this > arg ---> 1
	 */
	protected int compareTo(PVariable vf) {
		return variable.getName().compareTo(vf.variable.getName());
	}

	@Override
	public String toString() {
		return variable.getName();
	}

	@Override
	public Node toNode() throws ParseException {
		return pc.nf.buildVariableNode(variable);
	}

	@Override
	public PNodeI expand() {
		return this;
	}
}
