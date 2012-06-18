/* @author rich
 * Created on 02-Nov-2003
 */
package org.lsmp.djep.vectorJep.function;

import org.lsmp.djep.vectorJep.Dimensions;
import org.lsmp.djep.vectorJep.values.MatrixValueI;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

/**
 * A matrix enabled binary operator. This interface is primarilary used in the
 * matrixJep package but is here for convienience.
 * 
 * @author Rich Morris Created on 02-Nov-2003
 */
public interface BinaryOperatorI extends PostfixMathCommandI {
	/**
	 * Find the dimensions of this operator when applied to arguments with given
	 * dimensions.
	 */
	public Dimensions calcDim(Dimensions ldim, Dimensions rdim) throws ParseException;

	/**
	 * Calculates the value of this operator for given input with results stored
	 * in res. res is returned. Using this method is slightly faster than the
	 * standard run method as it eliminates the construction of tempoary
	 * objects.
	 */
	public MatrixValueI calcValue(MatrixValueI res, MatrixValueI lhs, MatrixValueI rhs) throws ParseException;
}
