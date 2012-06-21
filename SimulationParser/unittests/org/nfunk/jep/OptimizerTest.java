package org.nfunk.jep;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class OptimizerTest extends XJepTest {
	/**
	 * Create a test suite.
	 * 
	 * @return the TestSuite
	 */
	public static Test suite() {
		return new TestSuite(OptimizerTest.class);
	}

	public OptimizerTest() {
		super("Optimizer");
	}

	public void testOptimizerStatic() throws ParseException {
		simplifyTest("sin(13)+5", "5.420167036826641");
		
		j.addVariable("time", 0);
		simplifyTest("pulse(10,1,1)", "pulse(10,1,1)");

		j.addVariable("time", 100);
		simplifyTest("pulse(10,1,1)", "pulse(10,1,1)");
	}
	
	
	/**
	 * Main entry point.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		// Create an instance of this class and analyse the file

		TestSuite suite = new TestSuite(OptimizerTest.class);
		suite.run(new TestResult());
	}
}
