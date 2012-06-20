package ch.zhaw.simulation.jep.functions;

import java.util.Stack;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.Variable;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * Step function, return h for t > time else 0
 * 
 * TODO Unittest
 * 
 * @author Andreas Butti
 */
@Category(CategoryType.INTERVAL)
@Example("(h, t)")
@Description("Step function, return h for t > time else 0")
public class Step extends PostfixMathCommand implements TimeDependent {

	private JEP jep;

	public Step(JEP j) {
		super();
		numberOfParameters = 2;

		this.jep = j;
	}

	@Override
	public void run(Stack<Object> inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object t = inStack.pop();
		Object h = inStack.pop();

		if (!(t instanceof Number)) {
			throw new ParseException("step except a number as first parameter");
		}

		Variable vTime = jep.getVar("time");
		if (vTime == null) {
			throw new ParseException("step is missing global parameter time");
		}
		Object oTime = vTime.getValue();
		if (!(oTime instanceof Number)) {
			throw new ParseException("step expectiong global parameter time to be a double");
		}

		double time = ((Number) oTime).doubleValue();

		Object value = new Double(0);

		if (time >= ((Number) t).doubleValue()) {
			value = h;
		}

		inStack.push(value);
		return;
	}
}
