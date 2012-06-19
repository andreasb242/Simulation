package ch.zhaw.simulation.inexport;

public class ImportException extends Exception {
	private static final long serialVersionUID = 1L;

	public ImportException(String message) {
		super(message);
	}

	public ImportException(Throwable cause) {
		super(cause);
	}
}
