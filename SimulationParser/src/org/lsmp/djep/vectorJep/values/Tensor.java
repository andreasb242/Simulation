/* @author rich
 * Created on 07-Jul-2003
 */
package org.lsmp.djep.vectorJep.values;

import org.lsmp.djep.vectorJep.Dimensions;

/**
 * Represents tensor (generalisation of Matrix/Vector).
 * 
 * @author Rich Morris Created on 07-Jul-2003
 * @version 1.3.0.2 now extends number
 */
public class Tensor implements MatrixValueI {
	private Object values[] = null;
	private Dimensions dims;

	/** Creates a Tensor with the given dimension. **/
	public Tensor(Dimensions dims) {
		values = new Object[dims.numEles()];
		this.dims = dims;
	}

	/** Creates a Tensor with same dimension as the arguments. **/
	public Tensor(Tensor t) {
		values = new Object[t.getDim().numEles()];
		this.dims = t.getDim();
	}

	@Override
	public MatrixValueI copy() {
		Tensor tmp = new Tensor(this);
		tmp.setEles(tmp);
		return tmp;
	}

	/** Creates a tensor with dimensions [len,dims[0],...,dims[n]] **/
	public Tensor(int len, Dimensions dims) {
		values = new Object[len * dims.numEles()];
		this.dims = Dimensions.valueOf(len, dims);
	}

	@Override
	public Dimensions getDim() {
		return dims;
	}

	@Override
	public int getNumEles() {
		return values.length;
	}

	@Override
	public void setEle(int i, Object value) {
		values[i] = value;
	}

	@Override
	public Object getEle(int i) {
		return values[i];
	}

	/** sets the elements to those of the arguments. */
	@Override
	public void setEles(MatrixValueI val) {
		if (!dims.equals(val.getDim()))
			return;
		System.arraycopy(((Tensor) val).values, 0, values, 0, getNumEles());
	}

	/**
	 * Factory method to return a new Vector, Matrix or Tensor with the given
	 * dimensions.
	 */
	public static MatrixValueI getInstance(Dimensions dims) {
		switch (dims.rank()) {
		case 0:
			return new Scaler();
		case 1:
			return new MVector(dims.getFirstDim());
		case 2:
			return new Matrix(dims.getFirstDim(), dims.getLastDim());
		default:
			return new Tensor(dims);
		}
	}

	private int curEle = 0;

	/** Recursive procedure to print the tensor with lots of brackets. **/
	private void bufferAppend(StringBuffer sb, int currank) {
		sb.append("[");
		if (currank + 1 >= dims.rank()) {
			// bottom of tree
			for (int i = 0; i < dims.getIthDim(currank); ++i) {
				if (i != 0)
					sb.append(",");
				sb.append(getEle(curEle++));
			}
		} else {
			// not bottom of tree
			for (int i = 0; i < dims.getIthDim(currank); ++i) {
				if (i != 0)
					sb.append(",");
				bufferAppend(sb, currank + 1);
			}
		}
		sb.append("]");
	}

	/**
	 * Returns a string rep of tensor. Uses [[a,b],[c,d]] syntax.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		curEle = 0;
		bufferAppend(sb, 0);
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tensor))
			return false;
		Tensor tens = (Tensor) obj;
		if (!tens.getDim().equals(getDim()))
			return false;
		for (int i = 0; i < values.length; ++i)
			if (!values[i].equals(tens.values[i]))
				return false;
		return true;
	}

	/**
	 * Always override hashCode when you override equals. Efective Java, Joshua
	 * Bloch, Sun Press
	 */
	@Override
	public int hashCode() {
		int result = 17;
		for (int i = 0; i < values.length; ++i)
			result = 37 * result + values[i].hashCode();
		return result;
	}
}
