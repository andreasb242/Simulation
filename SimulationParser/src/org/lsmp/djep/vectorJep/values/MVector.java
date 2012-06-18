/* @author rich
 * Created on 07-Jul-2003
 */
package org.lsmp.djep.vectorJep.values;

import org.lsmp.djep.vectorJep.Dimensions;

/**
 * A Vector of elements.
 * 
 * @author Rich Morris Created on 07-Jul-2003
 * @version 1.3.0.2 now extends number
 */

public class MVector implements MatrixValueI {
	private Object data[] = null;
	private Dimensions dim;

	/** constructs a vector of a given size. **/
	public MVector(int size) {
		data = new Object[size];
		dim = Dimensions.valueOf(size);
	}

	/** Creates a vector of a given size. */
	public static MatrixValueI getInstance(int size) {
		return new MVector(size);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		for (int i = 0; i < data.length; ++i) {
			if (i > 0)
				sb.append(',');
			sb.append(data[i]);
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public Dimensions getDim() {
		return dim;
	}

	@Override
	public int getNumEles() {
		return data.length;
	}

	@Override
	public void setEle(int i, Object value) {
		data[i] = value;
	}

	@Override
	public Object getEle(int i) {
		return data[i];
	}

	/** sets the elements to those of the arguments. */
	@Override
	public void setEles(MatrixValueI val) {
		if (!dim.equals(val.getDim()))
			return;
		System.arraycopy(((MVector) val).data, 0, data, 0, getNumEles());
	}

	public Object[] getEles() {
		return data;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MVector))
			return false;
		MVector vec = (MVector) obj;
		if (!vec.getDim().equals(getDim()))
			return false;
		for (int i = 0; i < data.length; ++i)
			if (!data[i].equals(vec.data[i]))
				return false;
		return true;
	}

	/**
	 * Always override hashCode when you override equals. Effective Java, Joshua
	 * Bloch, Sun Press
	 */
	@Override
	public int hashCode() {
		int result = 17;
		for (int i = 0; i < data.length; ++i)
			result = 37 * result + data[i].hashCode();
		return result;
	}

	public MatrixValueI copy() {
		MVector tmp = new MVector(this.data.length);
		tmp.setEles(this);
		return tmp;
	}
}
