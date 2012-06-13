package ch.zhaw.simulation.sysintegration.filechooser.script;

import java.awt.Window;
import java.io.File;
import java.io.IOException;

import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.filechooser.FilechooserException;

public class WindowsFilechooser extends ScriptFilechooser {

	@Override
	public File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException {
		try {
			// Path is only accepted if we distribute a file (at least on Win7
			// this bug exists, see MSDN / Stackoverflow)
			String ext = filefilter.getExtension();
			if (ext == null) {
				ext = ".*";
			}

			String file = showFilechooser(filefilter, lastSavePath, "Unbenannt" + ext, false);
			return checkFileSaveExists(file, parent, filefilter, lastSavePath);
		} catch (Exception e) {
			throw new FilechooserException(e);
		}
	}

	@Override
	public File showOpenDialog(Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException {
		try {
			String ext = filefilter.getExtension();
			if (ext == null) {
				ext = "*";
			}

			String file = showFilechooser(filefilter, lastSavePath, "*." + ext, true);
			if (file == null) {
				return null;
			}
			return new File(file);
		} catch (Exception e) {
			throw new FilechooserException(e);
		}
	}

	private String showFilechooser(SimFileFilter filter, String openStartPath, String filename, boolean open) throws IOException, InterruptedException,
			FilechooserException {
		String ex = filter.getExtension();
		if (ex == null) {
			ex = ".*";
		}
		String filterText = filter.getDescription() + "|*" + ex;

		return run(new String[] { "filechooser/windows/filechooser.exe", open ? "open" : "save", filterText, openStartPath + "\\" + filename });
	}

}
