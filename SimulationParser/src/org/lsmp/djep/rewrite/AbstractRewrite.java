/* @author rich
 * Created on 01-Oct-2004
 */
package org.lsmp.djep.rewrite;

import org.lsmp.djep.xjep.NodeFactory;
import org.lsmp.djep.xjep.TreeUtils;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.OperatorSet;

/**
 * Basic implementation for a RewriteRule. Subclasses can have access to
 * NodeFactory,OperatorSet,TreeUtilsand XJep objects.
 * 
 * @author Rich Morris Created on 01-Oct-2004
 */
public abstract class AbstractRewrite implements RewriteRuleI {
	/** A NodeFactory used for construction nodes. */
	protected NodeFactory nf;
	/** An OperatorSet used for finding operators. */
	protected OperatorSet opSet;
	/** TreeUtils for testing types of nodes. */
	protected TreeUtils tu;
	/** A reference to main XJep opbject. */
	protected XJep xj;

	/**
	 * Constructor with reference to XJep object.
	 */
	protected AbstractRewrite(XJep xj) {
		opSet = xj.getOperatorSet();
		tu = xj.getTreeUtils();
		nf = xj.getNodeFactory();
		this.xj = xj;
	}
}
