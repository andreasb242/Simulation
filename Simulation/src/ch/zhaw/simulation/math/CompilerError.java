package ch.zhaw.simulation.math;
public class CompilerError extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	private int line;
	private int width;

	public CompilerError(String message, int line, int width) {
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
