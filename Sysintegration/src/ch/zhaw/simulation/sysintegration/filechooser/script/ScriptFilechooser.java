package ch.zhaw.simulation.sysintegration.filechooser.script;

import java.io.IOException;

import ch.zhaw.simulation.sysintegration.filechooser.AbstractFilechooser;
import ch.zhaw.simulation.sysintegration.filechooser.FilechooserException;

/**
 * Script Filechooser Baseclasse
 * 
 * @author Andreas Butti
 *
 * Return Codes:
 * 0: OK
 * 1: Wrong parameter count
 * 2: No File selected
 * 
 * If OK write <code>FileSelected:[Path] to STDOUT</code>
 * 
 */
public abstract class ScriptFilechooser extends AbstractFilechooser {

	protected String run(String[] cmd) throws IOException, InterruptedException, FilechooserException {
		Process process = Runtime.getRuntime().exec(cmd);

		FilechooserScriptOutputHandler stdout = new FilechooserScriptOutputHandler("[Filechooser] ", process.getInputStream(), System.out);
		OutputReaderThread stderr = new OutputReaderThread("[Filechooser] ", process.getErrorStream(), System.err);
		stderr.start();
		stdout.start();

		int result = process.waitFor();

		if (result == 1) {
			throw new FilechooserException("Wrong argument count");
		} else if (result == 2) {
			// no file selected, canceled
			return null;
		} else if (result != 0) {
			throw new FilechooserException("Unexpected result: " + result);
		}

		if (!"FileSelected".equals(stdout.getResult())) {
			throw new FilechooserException("Unexpected result: \"" + stdout.getResult() + "\"");
		}

		return stdout.getArgument();
	}

}
