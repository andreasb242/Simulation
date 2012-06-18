package ch.zhaw.simulation.gui.codeditor.library;

import ch.zhaw.simulation.math.Function;

public class LibraryFunctionNode extends LibraryNode<LibraryNode<?>> {

	private Function function;

	public LibraryFunctionNode(Function f) {
		this.function = f;
	}

	public Function getFunction() {
		return function;
	}

	@Override
	public String toString() {
		return this.function.getName() + this.function.getExample();
	}

}
