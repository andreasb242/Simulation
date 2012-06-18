/* @author rich
 * Created on 18-Nov-2003
 */
package ch.zhaw.simulation.jep.functions;

import java.util.Stack;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.Add;
import org.nfunk.jep.function.Divide;
import org.nfunk.jep.function.PostfixMathCommand;

import ch.zhaw.simulation.jep.Category;
import ch.zhaw.simulation.jep.CategoryType;
import ch.zhaw.simulation.jep.Description;
import ch.zhaw.simulation.jep.Example;

/**
 * A average function
 * 
 * @author Andreas Butti Created on 10-Sept-2012
 */
@Category(CategoryType.BASE)
@Example("(x, y, z, ...)")
@Description("The average of all parameter")
public class Avg extends PostfixMathCommand {
	private Add add;
	private Divide div;

	public Avg(JEP j) {
		super();
		add = (Add) j.getOperatorSet().getAdd().getPFMC();
		div = (Divide) j.getOperatorSet().getDivide().getPFMC();

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

			ret = add.add(ret, param);

			i++;
		}

		ret = div.div(ret, curNumberOfParameters);

		// push the result on the inStack
		stack.push(ret);
	}
}
