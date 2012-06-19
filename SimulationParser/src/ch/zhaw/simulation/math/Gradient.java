package ch.zhaw.simulation.math;

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;
import ch.zhaw.simulation.model.xy.DensityData;

@Category(CategoryType.SIMULATION)
@Example("(\"d0\", \"x\")")
@Description("The gradient of a density in a direction (x or y)")
public class Gradient extends PostfixMathCommand implements CallbackEvaluationI {
	private Vector<String> density = new Vector<String>();

	public Gradient(Vector<DensityData> density) {
		super();
		this.numberOfParameters = 2;

		for (DensityData d : density) {
			this.density.add(d.getName());
		}
	}

	@Override
	public void run(Stack<Object> s) throws ParseException {
		String density = s.get(0).toString();
		String direction = s.get(1).toString();

		if (!this.density.contains(density)) {
			throw new ParseException("Density «" + density + "» not allowed");
		}

		if (!("x".equals(direction) || "y".equals(direction))) {
			throw new ParseException("grad: unknown direction «" + direction + "» only \"x\" and \"y\" allowed");
		}
	}

	/**
	 * Hack to pass by an function without a real number-value.
	 */
	@Override
	public Object evaluate(Node node, EvaluatorI pv) throws ParseException {
		return null;
	}
}
