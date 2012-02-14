package org.nfunk.jep;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.nfunk.jep.type.Complex;

public class ComplexTest extends TestCase {

	public ComplexTest(String name) {
		super(name);
	}

	/**
	 * Test method for 'org.nfunk.jep.function.Logarithm.run(Stack)' Tests the
	 * return value of log(NaN). This is a test for bug #1177557
	 */
	public void testPower() {
		Complex one = new Complex(1, 0);
		Complex negOne = new Complex(-1, 0);
		Complex two = new Complex(2, 0);

		// multiplication
		Assert.assertTrue((one.mul(one)).equals(one, 0));
		Assert.assertTrue((one.mul(negOne)).equals(negOne, 0));
		Assert.assertTrue((negOne.mul(one)).equals(negOne, 0));
		// power
		Assert.assertTrue((one.power(one)).equals(one, 0));
		Assert.assertTrue((one.power(-1)).equals(one, 0));
		Assert.assertTrue((one.power(negOne)).equals(one, 0));
		Assert.assertTrue((negOne.power(two)).equals(one, 0));
		// Assert.assertTrue((negEight.power(1.0/3)).equals(negTwo,0));
	}

}
