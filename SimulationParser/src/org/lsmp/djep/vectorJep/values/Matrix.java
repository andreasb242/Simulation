/* @author rich
 * Created on 07-Jul-2003
 */
package org.lsmp.djep.vectorJep.values;

import org.lsmp.djep.vectorJep.Dimensions;

/**
 * Represents a matrix.
 * 
 * @author Rich Morris Created on 07-Jul-2003
 * @version 2.3.0.2 now extends number
 * @version 2.3.1.1 Bug with non square matrices fixed.
 * @since 2.3.2 Added equals method.
 */
public class Matrix implements MatrixValueI {
	@Override
	public MatrixValueI copy() {
		Matrix tmp = new Matrix(this.rows, this.cols);
		tmp.setEles(this);
		return tmp;
	}

	// want package access to simplify addition of matrices
	private int rows = 0;
	private int cols = 0;
	private Object data[][] = null;
	private Dimensions dims;

	/** Construct a matrix with given rows and cols. */
	protected Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		data = new Object[rows][cols];
		dims = Dimensions.valueOf(rows, cols);
	}

	public static MatrixValueI getInstance(int rows, int cols) {
		return new Matrix(rows, cols);
	}

	/**
	 * Returns a string representation of matrix. Uses [[a,b],[c,d]] syntax.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		for (int i = 0; i < rows; ++i) {
			if (i > 0)
				sb.append(',');
			sb.append('[');
			for (int j = 0; j < cols; ++j) {
				if (j > 0)
					sb.append(',');
				sb.append(data[i][j]);
			}
			sb.append(']');
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public Dimensions getDim() {
		return dims;
	}

	@Override
	public int getNumEles() {
		return rows * cols;
	}

	public int getNumRows() {
		return rows;
	}

	public int getNumCols() {
		return cols;
	}

	@Override
	public void setEle(int n, Object value) {
		int i = n / cols;
		int j = n % cols;
		data[i][j] = value;
	}

	public void setEle(int i, int j, Object value) {
		data[i][j] = value;
	}

	@Override
	public Object getEle(int n) {
		int i = n / cols;
		int j = n % cols;
		return data[i][j];
	}

	public Object getEle(int i, int j) {
		return data[i][j];
	}

	public Object[][] getEles() {
		return data;
	}

	/** sets the elements to those of the arguments. */
	@Override
	public void setEles(MatrixValueI val) {
		if (!dims.equals(val.getDim()))
			return;
		for (int i = 0; i < rows; ++i)
			System.arraycopy(((Matrix) val).data[i], 0, data[i], 0, cols);
	}

	/**
	 * Are two matrices equal, element by element Overrides Object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Matrix))
			return false;
		Matrix mat = (Matrix) obj;
		if (!mat.getDim().equals(getDim()))
			return false;
		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < cols; ++j)
				if (!data[i][j].equals(mat.data[i][j]))
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
		// long xl = Double.doubleToLongBits(this.re);
		// long yl = Double.doubleToLongBits(this.im);
		// int xi = (int)(xl^(xl>>32));
		// int yi = (int)(yl^(yl>>32));
		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < cols; ++j)
				result = 37 * result + data[i][j].hashCode();
		return result;
	}

}
