package ch.zhaw.simulation.filehandling;

import java.io.File;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.plugin.PluginDescription;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.inexport.ImportReader;
import ch.zhaw.simulation.sysintegration.SimFileFilter;

public class LoadSaveHandler {
	private File path;
	private FlowEditorControl control;
	private SimzSaver saver = new SimzSaver();
	private SimzLoader loader = new SimzLoader();

	private static final String LAST_SAVEPATH = "opensave.lastpath";

	public LoadSaveHandler(FlowEditorControl control) {
		this.control = control;
	}

	private String getLastSavePath() {
		return control.getSettings().getSetting(LAST_SAVEPATH, System.getProperty("user.home"));
	}

	private void setLastSavePath(File f) {
		control.getSettings().setSetting(LAST_SAVEPATH, f.getParentFile().getAbsolutePath());
	}

	public boolean save() {
		if (path == null || !path.getName().endsWith(".simz")) {
			return saveAs();
		} else {
			if (!checkCanSave(path)) {
				return false;
			}
			if (!doSave(path)) {
				errorSaveFile();
				return false;
			} else {
				control.setStatusText("Datei gespeichert");
				return true;
			}
		}
	}

	private void errorSaveFile() {
		Messagebox msg = new Messagebox(control.getParent(), "Fehler beim Speichern",
				"<html>Die Datei konnte nicht gespeichert werden!<br>Versuchen Sie die Datei an einen anderen Ort zu speichern.</html>", Messagebox.ERROR);
		msg.addButton("Speichern untern", 0);
		msg.addButton("OK", 1, true);
		if (msg.display() == 0) {
			saveAs();
		}
	}

	private boolean checkCanSave(File file) {
		boolean canWrite = true;

		if (!file.exists()) {
			try {
				canWrite = file.createNewFile();

				if (canWrite) {
					canWrite = file.canWrite();
				}
			} catch (Exception e) {
				Errorhandler.logError(e, "Create file failed");
				canWrite = false;
			}
		} else {
			canWrite = file.canWrite();
		}

		if (!canWrite) {
			Messagebox msg = new Messagebox(control.getParent(), "Schreibgeschützt", "Die Datei \"" + file.getAbsolutePath() + "\" ist schreibgeschützt.",
					Messagebox.WARNING);
			msg.addButton("Anderswo speichern", 0);
			msg.addButton("Abbrechen", 1);

			if (msg.display() == 0) {
				saveAs();
			}
			return false;
		}
		return true;
	}

	private boolean doSave(File file) {
		try {
			saver.save(file, control.getModel());
			control.getModel().setSaved();
			return true;
		} catch (Exception e) {
			Errorhandler.logError(e, "Save failed");
			return false;
		}
	}

	public boolean saveAs() {
		File f = control.getSysintegration().showSaveDialog(control.getParent(), simulationXMLSave, getLastSavePath());
		if (f != null) {
			setLastSavePath(f);

			if (!doSave(f)) {
				errorSaveFile();
				return false;
			} else {
				control.setStatusText("Datei gespeichert");
				path = f;
				return true;
			}
		}
		return false;
	}

	public boolean open() {
		File f = control.getSysintegration().showOpenDialog(control.getParent(), control.getImportPlugins().getSimulationFileOpen(), getLastSavePath());

		if (f != null) {
			if (!f.isFile()) {
				Messagebox.showError(control.getParent(), "Öffnen fehlgeschlagen", "Dies ist keine Datei!");
				return false;
			}
			return open(f);
		}
		return true;
	}

	public boolean open(File file) {
		if (!file.canRead()) {
			Messagebox msg = new Messagebox(control.getParent(), "Fehler beim Öffnen", "Die Datei \"" + file.getAbsolutePath()
					+ "\" kann nicht gelesen werden, keine Rechte!", Messagebox.ERROR);
			msg.addButton("andere Datei öffnen", 0);
			msg.addButton("Abbrechen", 1, true);
			if (msg.display() == 0) {
				return open();
			}
			return false;
		}

		// /////////////////////////
		// import other files
		// /////////////////////////

		for (PluginDescription<ImportReader> plugin : control.getImportPlugins().getPlugins()) {
			ImportReader handler = plugin.getPlugin();

			try {
				if (handler.canHandle(file)) {
					handler.read(file);
					control.stopAutoparser();
					if (handler.load(control.getModel())) {
						path = file;
						control.setStatusText("Datei importiert");
						control.getModel().setSaved();
						setLastSavePath(file);

						control.initMetadata();
						return true;
					} else {
						Messagebox msg = new Messagebox(control.getParent(), "Fehler beim Import",
								"Die Datei konnte nicht gelesen werden. Ggf. ist die Datei beschädigt oder vom falschen Format.", Messagebox.ERROR);
						msg.addButton("OK", 0, true);
						msg.display();
						return false;
					}
				}
			} catch (Exception e) {
				Errorhandler.showError(e);
				return false;
			} finally {
				control.startAutoparser();
			}
		}

		// /////////////////////////
		// end import
		// /////////////////////////

		try {
			loader.open(file);
		} catch (Exception e) {
			Messagebox msg = new Messagebox(control.getParent(), "Fehler beim Öffnen", "Die Datei \"" + file.getAbsolutePath()
					+ "\" konnte nicht gelesen werden!\n" + e.getMessage(), Messagebox.ERROR);
			msg.addButton("OK", 0, true);
			msg.display();
			return false;
		}

		if (!loader.versionCompatible()) {
			Messagebox msg = new Messagebox(control.getParent(), "Fehler beim Öffnen",
					"Die Datei ist von einer neuen Vesion des Programms erstellt worden und kann nicht gelesen werden!", Messagebox.ERROR);
			msg.addButton("OK", 0, true);
			msg.display();
			return false;
		}

		if (!loader.versionOk()) {
			Messagebox msg = new Messagebox(
					control.getParent(),
					"Warnung",
					"<html>Die Datei ist von einer neuen Vesion des Programms erstellt worden!<br>"
							+ "Möglicherweise können nicht alle Daten eingelesen werden,<br>erstellen Sie eine Sicherungskopie bevor Sie diese Datei überschreiben!</html>",
					Messagebox.WARNING);
			msg.addButton("Laden abbrechen", 0);
			msg.addButton("OK", 1, true);
			if (msg.display() == 0) {
				return false;
			}
		}

		setLastSavePath(file);

		try {
			control.stopAutoparser();

			if (!loader.load(control.getModel())) {
				Messagebox.showWarning(control.getParent(), "Probleme beim lesen",
						"Die Datei enthielt Fehler und konnte möglicherweise nicht korrekt eingelesen werden.");
			}
			path = file;
			control.setStatusText("Datei geladen");
			control.getModel().setSaved();
		} catch (Exception e) {
			Messagebox msg = new Messagebox(control.getParent(), "Fehler beim Öffnen",
					"Die Datei konnte nicht gelesen werden. Ggf. ist die Datei beschädigt oder vom falschen Format.\n" + e.getMessage(), Messagebox.ERROR);
			msg.addButton("OK", 0, true);
			msg.display();

			return false;
		} finally {
			control.startAutoparser();
		}
		return true;
	}

	public File getPath() {
		return path;
	}

	private SimFileFilter simulationXMLSave = new SimFileFilter() {
		@Override
		public boolean accept(File f) {
			return (f.isDirectory() || f.getName().endsWith(".simz"));
		}

		@Override
		public String getDescription() {
			return "Simulationsdatei (.simz)";
		}

		@Override
		public String getExtension() {
			return ".simz";
		}
	};

	public void clear() {
		path = null;
	}
}
