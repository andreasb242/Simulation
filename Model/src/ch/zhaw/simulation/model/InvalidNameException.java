package ch.zhaw.simulation.model;

public class InvalidNameException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidNameException(String message) {
		super(message);
	}
}
