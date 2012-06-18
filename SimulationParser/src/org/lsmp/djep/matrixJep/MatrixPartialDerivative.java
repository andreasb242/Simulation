/* @author rich
 * Created on 29-Oct-2003
 */
package org.lsmp.djep.matrixJep;

import org.lsmp.djep.djep.PartialDerivative;
import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.lsmp.djep.vectorJep.values.Tensor;
import org.nfunk.jep.Node;

/**
 * Contains information about a PartialDerivative of a variable.
 * 
 * @author Rich Morris Created on 29-Oct-2003 TODO_YEP Should setValue be
 *         overwritten?
 */
public class MatrixPartialDerivative extends PartialDerivative implements MatrixVariableI {
	private MatrixValueI mvalue = null;

	/**
	 * Protected constructor, should only be constructed through the
	 * findDerivative method in {@link MatrixVariable}.
	 **/
	protected MatrixPartialDerivative(MatrixVariable var, String derivnames[], Node deriv) {
		super(var, derivnames);
		/*
		 * TODO_YEP could be a little cleverer just have a partial derivative
		 * which is a constant dy/dx = 1 don't use an equation, instead use a
		 * value.
		 * 
		 * if(deriv instanceof ASTMConstant) { MatrixValueI val =
		 * ((ASTMConstant) deriv).getMValue();
		 * System.out.println("Warning constant derivative "+getName()+"="+val);
		 * mvalue = (Scaler) Scaler.getInstance(val); } else {
		 */
		setEquation(deriv);
		setValidValue(false);
		mvalue = Tensor.getInstance(var.getDimensions());
	}

	@Override
	public Dimensions getDimensions() {
		MatrixVariableI root = (MatrixVariableI) getRoot();
		return root.getDimensions();
	}

	@Override
	public void setDimensions(Dimensions dims) {
	}

	@Override
	public MatrixValueI getMValue() {
		return mvalue;
	}

	@Override
	public void setMValue(MatrixValueI val) {
		if (this.isConstant()) {
			return;
		}
		mvalue.setEles(val);
		setValidValue(true);
		setChanged();
		notifyObservers();
	}
}
