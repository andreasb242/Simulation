package ch.zhaw.simulation.sysintegration.filechooser;

public class FilechooserException extends Exception {
	private static final long serialVersionUID = 1L;

	public FilechooserException(String message) {
		super(message);
	}

	public FilechooserException(Exception reason) {
		super(reason);
	}

}
