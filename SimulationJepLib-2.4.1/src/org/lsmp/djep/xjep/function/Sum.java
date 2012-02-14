/* @author rich
 * Created on 18-Nov-2003
 */
package org.lsmp.djep.xjep.function;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.Add;

/**
 * A sum function Sum(x^2,x,1,10) finds the sum of x^2 with x running from 1 to
 * 10. Sum(x^2,x,1,10,2) calculates the 1^2+3^2+5^2+7^2+9^2 i.e. in steps of 2.
 * 
 * @author Rich Morris Created on 10-Sept-2004
 */
public class Sum extends SumType {

	private Add add;

	public Sum(JEP j) {
		super("Sum");
		add = (Add) j.getOperatorSet().getAdd().getPFMC();
	}

	public Object evaluate(Object elements[]) throws ParseException {
		Object ret;
		ret = elements[0];
		for (int i = 1; i < elements.length; ++i) {
			ret = add.add(ret, elements[i]);
		}
		return ret;
	}
}
