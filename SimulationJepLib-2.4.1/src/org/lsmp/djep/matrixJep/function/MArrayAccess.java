/*
Created 17-May-2006 - Richard Morris
 */
package org.lsmp.djep.matrixJep.function;

import org.lsmp.djep.vectorJep.function.ArrayAccess;
import org.lsmp.djep.xjep.PrintVisitor;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class MArrayAccess extends ArrayAccess implements PrintVisitor.PrintRulesI {

	public void append(Node node, PrintVisitor pv) throws ParseException {
		node.jjtGetChild(0).jjtAccept(pv, null);
		node.jjtGetChild(1).jjtAccept(pv, null);
	}

	public MArrayAccess() {
		super();
	}

}
