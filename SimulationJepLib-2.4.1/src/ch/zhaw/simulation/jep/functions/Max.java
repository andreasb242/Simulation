/* @author rich
 * Created on 18-Nov-2003
 */
package ch.zhaw.simulation.jep.functions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.Comparative;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * A max function
 * 
 * Returns the maximum value
 * 
 * @author Andras Butti on 18-Jul-2012
 */
@Category(CategoryType.BASE)
@Example("(x, y, z, ...)")
@Description("the maximum value")
public class Max extends PostfixMathCommand {
	private static Comparative comp = new Comparative(Comparative.LE);

	public Max() {
		super();

		// Use a variable number of arguments
		numberOfParameters = -1;
	}

	@Override
	public void run(Stack<Object> stack) throws ParseException {
		checkStack(stack);// check the stack

		if (curNumberOfParameters < 1) {
			throw new ParseException("No arguments for Min");
		}

		// initialize the result to the first argument
		Object ret = stack.pop();
		Object param;
		int i = 1;

		// repeat summation for each one of the current parameters
		while (i < curNumberOfParameters) {
			// get the parameter from the stack
			param = stack.pop();

			if (comp.gt(param, ret)) {
				ret = param;
			}

			i++;
		}

		// push the result on the inStack
		stack.push(ret);
	}

}
