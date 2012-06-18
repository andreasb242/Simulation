/* @author rich
 * Created on 04-Nov-2003
 */
package org.lsmp.djep.vectorJep.values;

import org.lsmp.djep.vectorJep.Dimensions;
import org.nfunk.jep.type.Complex;

/**
 * Degenerate i.e. rank 0 Tensor. Just has a single element.
 * 
 * @author Rich Morris Created on 04-Nov-2003
 * @version 1.3.0.2 now extends number
 * 
 *          TODO_YEP don't implement number! So what if a scaler is a boolean
 */
public class Scaler extends Number implements MatrixValueI {

	private static final long serialVersionUID = 336717881577257953L;
	private Object value;

	protected Scaler() {
		value = new Double(0.0);
	}

	private Scaler(Object o) {
		value = o;
	}

	public static MatrixValueI getInstance(Object o) {
		return new Scaler(o);
	}

	@Override
	public Dimensions getDim() {
		return Dimensions.ONE;
	}

	@Override
	public int getNumEles() {
		return 1;
	}

	@Override
	public void setEle(int i, Object value) {
		if (value != null)
			this.value = value;
	}

	@Override
	public Object getEle(int i) {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	/** sets the elements to those of the arguments. */
	@Override
	public void setEles(MatrixValueI val) {
		if (!(val.getDim().equals(Dimensions.ONE))) {
			return;
		}

		value = val.getEle(0);
	}

	/** value of constant coeff. */
	@Override
	public int intValue() {
		if (value instanceof Complex) {
			return ((Complex) value).intValue();
		}

		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1 : 0;
		}

		// throws a cast exception if not a number
		return ((Number) value).intValue();
	}

	/** value of constant coeff. */
	@Override
	public long longValue() {
		if (value instanceof Complex) {
			return ((Complex) value).longValue();
		}

		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1l : 0l;
		}

		// throws a cast exception if not a number
		return ((Number) value).longValue();
	}

	/** value of constant coeff. */
	@Override
	public float floatValue() {
		if (value instanceof Complex) {
			return ((Complex) value).floatValue();
		}

		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1f : 0f;
		}

		// throws a cast exception if not a number
		return ((Number) value).floatValue();
	}

	/** value of constant coeff. */
	@Override
	public double doubleValue() {
		if (value instanceof Complex) {
			return ((Complex) value).doubleValue();
		}
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1d : 0d;
		}

		// throws a cast exception if not a number
		return ((Number) value).doubleValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Scaler))
			return false;

		Scaler s = (Scaler) obj;

		if (!s.getDim().equals(getDim())) {
			return false;
		}

		if (!value.equals(s.value)) {
			return false;
		}
		return true;
	}

	/**
	 * Always override hashCode when you override equals. Effective Java, Joshua
	 * Bloch, Sun Press
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + value.hashCode();
		return result;
	}

	@Override
	public MatrixValueI copy() {
		return new Scaler(value);
	}
}
