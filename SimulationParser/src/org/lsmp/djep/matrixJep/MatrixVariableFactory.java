/* @author rich
 * Created on 19-Dec-2003
 */
package org.lsmp.djep.matrixJep;

import org.lsmp.djep.djep.DVariable;
import org.lsmp.djep.djep.DVariableFactory;
import org.lsmp.djep.djep.PartialDerivative;
import org.nfunk.jep.Node;
import org.nfunk.jep.Variable;

/**
 * Allows creation of matrix aware variables.
 * 
 * @author Rich Morris Created on 19-Dec-2003
 */
public class MatrixVariableFactory extends DVariableFactory {

	/** create a derivative */
	@Override
	public PartialDerivative createDerivative(DVariable var, String[] dnames, Node eqn) {
		return null;
	}

	/** Create a variable with a given value. */
	@Override
	public Variable createVariable(String name, Object value) {
		return new MatrixVariable(name, value);
	}

	/** Create a variable with a given value. */
	@Override
	public Variable createVariable(String name) {
		return new MatrixVariable(name);
	}

}
