/* @author rich
 * Created on 22-Apr-2005
 *
 * See LICENSE.txt for license information.
 */
package org.nfunk.jep;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.lsmp.djep.matrixJep.MatrixJep;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * @author Rich Morris Created on 22-Apr-2005
 */
public class ButtiEditTest extends DJepTest {
	public ButtiEditTest(String name) {
		super(name);
	}

	/**
	 * Create a test suite.
	 * 
	 * @return the TestSuite
	 */
	public static Test suite() {
		return new TestSuite(ButtiEditTest.class);
	}

	/**
	 * Main entry point.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		// Create an instance of this class and analyse the file

		TestSuite suite = new TestSuite(ButtiEditTest.class);
		suite.run(new TestResult());
	}

	protected void setUp() {
		j = new MatrixJep();
		j.addStandardConstants();
		j.addStandardFunctions();
		j.addComplex();
		// j.setTraverse(true);
		j.setAllowAssignment(true);
		j.setAllowUndeclared(true);
		j.setImplicitMul(true);
		((MatrixJep) j).addStandardDiffRules();
	}

	public Object calcValue(Node node) throws ParseException {
		Node matEqn = ((MatrixJep) j).preprocess(node);
		Object res = ((MatrixJep) j).evaluate(matEqn);
		return res;
	}

	public void valueTest(String expr, double a) throws Exception {
		valueTest(expr, new Double(a));
	}

	public void simplifyTestString(String expr, String expected) throws ParseException {
		XJep xj = (XJep) j;

		Node node = xj.parse(expr);
		Node processed = xj.preprocess(node);
		Node simp = xj.simplify(processed);
		String res = xj.toString(simp);

		if (!expected.equals(res))
			System.out.println("Error: Value of \"" + expr + "\" is \"" + res + "\" should be \"" + expected + "\"");
		assertEquals("<" + expr + ">", expected, res);
		System.out.println("Success: Value of \"" + expr + "\" is \"" + res + "\"");
	}

	public void testSimplify() throws Exception {
		simplifyTestString("0-x", "-x");
	}
}
