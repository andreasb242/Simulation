/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package org.nfunk.jep;

import java.util.Hashtable;

import org.nfunk.jep.function.PostfixMathCommandI;

/*
 * A Hashtable which holds a list of functions.
 */
public class FunctionTable extends Hashtable<String, PostfixMathCommandI> {
	private static final long serialVersionUID = -1192898221311853572L;

	public FunctionTable() {
	}
}
