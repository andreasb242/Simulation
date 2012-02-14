package ch.zhaw.simulation.math;

public class UserException extends Exception {
	private static final long serialVersionUID = 1L;

	public UserException(String message) {
		super(message);
	}

	public static class NotUsedException extends UserException {
		private static final long serialVersionUID = 1L;

		public NotUsedException(String text) {
			super(text);
		}
	}
}
