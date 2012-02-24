/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package org.lsmp.djep.xjep;

import java.util.Enumeration;

import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;
import org.nfunk.jep.VariableFactory;

/**
 * An extension of the symbol table with a few new features.
 * 
 * @author Rich Morris Created on 18-Mar-2004
 */
public class XSymbolTable extends SymbolTable {
	private static final long serialVersionUID = 741560154912130566L;

	/**
	 * Create a new XSymbolTable with the given variable factory.
	 */
	public XSymbolTable(VariableFactory varFac) {
		super(varFac);
	}

	/** Creates a new SymbolTable with the same variable factory as this. */
	public SymbolTable newInstance() {
		return new XSymbolTable(this.getVariableFactory());
	}

	/**
	 * Prints the contents of the symbol table displaying its equations and
	 * value.
	 */
	public void print(PrintVisitor pv) {
		for (Enumeration<Variable> e = this.elements(); e.hasMoreElements();) {
			XVariable var = (XVariable) e.nextElement();
			pv.append(var.toString(pv) + "\n");
			// TODO_YEP watch out for possible conflict with overriding pv's string
			// buffer
		}
	}

	/**
	 * Copy the values of all constants into this from the supplied symbol
	 * table.
	 */
	public void copyConstants(SymbolTable symTab) {
		for (Variable var : symTab.values()) {
			if (var.isConstant()) {
				this.addConstant(var.getName(), var.getValue());
			}
		}
	}
}
