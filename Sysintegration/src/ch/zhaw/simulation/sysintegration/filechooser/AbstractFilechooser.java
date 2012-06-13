package ch.zhaw.simulation.sysintegration.filechooser;

import java.awt.Window;
import java.io.File;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.sysintegration.SimFileFilter;

public abstract class AbstractFilechooser implements Filechooser {

	public File checkFileSaveExists(String file, Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException {
		File f;
		if (!file.endsWith(filefilter.getExtension())) {
			f = new File(file + filefilter.getExtension());
		} else {
			f = new File(file);
		}

		if (f.exists()) {
			Messagebox msg = new Messagebox(parent, "Überschreiben", "Soll die Datei \"" + f.getAbsolutePath() + "\" überschreiben werden?",
					Messagebox.QUESTION);
			msg.addButton("Speichern unter", 0);
			msg.addButton("Abbrechen", 1);
			msg.addButton("Überschreiben", 2, true);

			int res = msg.display();
			if (res == 0) {
				f = showSaveDialog(parent, filefilter, lastSavePath);
			} else if (res != 2) {
				return null;
			}

		}

		return checkCanSave(f, parent, filefilter, lastSavePath);
	}

	protected File checkCanSave(File file, Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException {
		boolean canWrite = true;

		if (!file.exists()) {
			try {
				canWrite = file.createNewFile();

				if (canWrite) {
					canWrite = file.canWrite();
				}
			} catch (Exception e) {
				Errorhandler.logError(e);
				canWrite = false;
			}
		} else {
			canWrite = file.canWrite();
		}

		if (!canWrite) {
			Messagebox msg = new Messagebox(parent, "Schreibgeschützt", "Die Datei \"" + file.getAbsolutePath() + "\" ist schreibgeschützt.",
					Messagebox.WARNING);
			msg.addButton("Anderswo speichern", 0);
			msg.addButton("Abbrechen", 1);

			if (msg.display() == 0) {
				return showSaveDialog(parent, filefilter, lastSavePath);
			}
			return null;
		}
		return file;
	}

}
