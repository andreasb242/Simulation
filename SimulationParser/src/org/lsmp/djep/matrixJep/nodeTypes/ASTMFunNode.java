/* @author rich
 * Created on 01-Feb-2004
 */
package org.lsmp.djep.matrixJep.nodeTypes;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.vectorJep.values.Tensor;
import org.nfunk.jep.ASTFunNode;

/**
 * @author Rich Morris Created on 01-Feb-2004
 */
public class ASTMFunNode extends ASTFunNode implements MatrixNodeI {
	private MatrixValueI mvar = null;

	public ASTMFunNode(int i) {
		super(i);
	}

	@Override
	public Dimensions getDim() {
		return mvar.getDim();
	}

	public void setDim(Dimensions dim) {
		mvar = Tensor.getInstance(dim);
	}

	@Override
	public MatrixValueI getMValue() {
		return mvar;
	}

}
