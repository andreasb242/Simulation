package ch.zhaw.simulation.gui.codeditor.library;

import ch.zhaw.simulation.math.Constant;

public class LibraryConstNode extends LibraryNode<LibraryNode<?>> {

	private Constant c;

	public LibraryConstNode(Constant c) {
		this.c = c;
	}

	public Constant getConst() {
		return c;
	}
	
	@Override
	public String toString() {
		return c.name;
	}

}
