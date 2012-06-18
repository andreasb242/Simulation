/* @author rich
 * Created on 28-Feb-2004
 */
package org.lsmp.djep.xjep;

import org.nfunk.jep.Variable;
import org.nfunk.jep.VariableFactory;

/**
 * A VariableFactory which creates XVariables (which have equations).
 * 
 * @author Rich Morris Created on 28-Feb-2004
 */
public class XVariableFactory extends VariableFactory {

	@Override
	public Variable createVariable(String name, Object value) {
		return new XVariable(name, value);
	}

	@Override
	public Variable createVariable(String name) {
		return new XVariable(name);
	}
}
