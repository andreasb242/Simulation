/* @author rich
 * Created on 01-Feb-2004
 */
package org.lsmp.djep.matrixJep.nodeTypes;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.nfunk.jep.Node;

/**
 * @author Rich Morris Created on 01-Feb-2004
 */
public interface MatrixNodeI extends Node {
	public Dimensions getDim();

	public MatrixValueI getMValue();
}
