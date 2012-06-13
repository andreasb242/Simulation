package ch.zhaw.simulation.sysintegration.filechooser.script;

import java.io.InputStream;
import java.io.PrintStream;

public class FilechooserScriptOutputHandler extends OutputReaderThread {
	private String result;
	private String argument;
	private boolean found = false;

	public FilechooserScriptOutputHandler(String prefix, InputStream input, PrintStream out) {
		super(prefix, input, out);
	}

	@Override
	protected void handleLine(String line) {
		super.handleLine(line);

		if (found) {
			return;
		}

		int pos = line.indexOf(":");

		if (pos < 0) {
			this.result = line;
			return;
		}

		found = true;

		this.result = line.substring(0, pos);
		this.argument = line.substring(pos + 1).trim();
	}

	public String getResult() {
		return result;
	}

	public String getArgument() {
		return argument;
	}
}
