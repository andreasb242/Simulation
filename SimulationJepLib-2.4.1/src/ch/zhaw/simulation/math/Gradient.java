package ch.zhaw.simulation.math;

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class Gradient extends PostfixMathCommand {
	private Vector<String> density = new Vector<String>();

	public Gradient() {
		super();
		this.numberOfParameters = 2;

		density.add("ab");
		density.add("density");
	}

	@Override
	public void run(Stack<Object> s) throws ParseException {
		String density = s.get(0).toString();
		String direction = s.get(1).toString();

		if (!this.density.contains(density)) {
			throw new ParseException("Density «" + density + "» not allowed");
		}

		if(!("x".equals(direction) || "y".equals(direction))) {
			throw new ParseException("grad: unknown direction «" + direction + "» only \"x\" and \"y\" allowed");
		}
	}
}
