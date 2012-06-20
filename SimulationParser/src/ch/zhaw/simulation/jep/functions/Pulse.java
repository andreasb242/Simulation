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
 * pulse(v, f, r) Impulses of volume v, first time f, repeat interval r
 * 
 * TODO Unittests
 * 
 * @author Andreas Butti
 */
@Category(CategoryType.INTERVAL)
@Example("(v, f, r)")
@Description("Impulses of volume v, first time f, repeat interval r")
public class Pulse extends PostfixMathCommand implements TimeDependent {
	private double nextTime = Double.NaN;
	private double lastTime = 0;

	private JEP jep;

	public Pulse(JEP j) {
		super();
		numberOfParameters = 3;

		this.jep = j;
	}

	@Override
	public void run(Stack<Object> stack) throws ParseException {
		checkStack(stack);// check the stack

		Object ro = stack.pop();
		Object fo = stack.pop();
		Object vo = stack.pop();

		if (!(fo instanceof Number && ro instanceof Number)) {
			throw new ParseException("pulse except a number as type of parameter 2 and 3");
		}

		Variable vTime = jep.getVar("time");
		if (vTime == null) {
			throw new ParseException("pulse is missing global parameter time");
		}
		Object oTime = vTime.getValue();
		if (!(oTime instanceof Number)) {
			throw new ParseException("pulse expectiong global parameter time to be a double");
		}

		double time = ((Number) oTime).doubleValue();

		if (Double.isNaN(nextTime) || lastTime > time) {
			nextTime = ((Number) fo).doubleValue();
			lastTime = time;
		}

		Object pulse = new Double(0);

		if (time >= nextTime) {
			pulse = vo;
			nextTime += ((Number) ro).doubleValue();
		}

		stack.push(pulse);
	}

}
