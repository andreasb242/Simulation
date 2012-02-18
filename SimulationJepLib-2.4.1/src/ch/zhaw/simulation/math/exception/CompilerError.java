package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.SimulationObject;

public class CompilerError extends SimulationModelException {
	private static final long serialVersionUID = 1L;
	private String message;
	private int line;
	private int width;

	public CompilerError(SimulationObject o, String message, int line, int width) {
		super(o);
		this.message = message;
		this.line = line;
		this.width = width;
	}

	public String getMessage() {
		return message;
	}

	public int getLine() {
		return line;
	}

	public int getWidth() {
		return width;
	}
}
