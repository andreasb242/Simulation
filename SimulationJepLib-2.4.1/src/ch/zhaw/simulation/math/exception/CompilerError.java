package ch.zhaw.simulation.math.exception;

public class CompilerError extends SimulationModelException {
	private static final long serialVersionUID = 1L;
	private String message;
	private int line;
	private int width;

	public CompilerError(Object simObject, String message, int line, int width) {
		super(simObject);
		this.message = message;
		this.line = line;
		this.width = width;
	}

	@Override
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
