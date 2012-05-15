package ch.zhaw.simulation.math;

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.function.PostfixMathCommand;

public class Gradient extends PostfixMathCommand implements CallbackEvaluationI {
	private Vector<String> density = new Vector<String>();

	public Gradient() {
		super();
		this.numberOfParameters = 2;

		// TODO: dynamic add densities!!!!
		density.add("d0");
		density.add("d1");
		density.add("d2");
		density.add("d3");
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

	/**
	 * Hack to pass by an function without a real number-value.
	 */
	public Object evaluate(Node node, EvaluatorI pv) throws ParseException {
		return null;
	}
}
