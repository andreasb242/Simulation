package ch.zhaw.simulation.math;

import org.nfunk.jep.function.PostfixMathCommandI;

public class Function {
	private String name;
	private PostfixMathCommandI command;

	public Function(String name, PostfixMathCommandI command) {
		this.name = name;
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public Class<? extends PostfixMathCommandI> getFunctionClass() {
		return command.getClass();
	}
}
