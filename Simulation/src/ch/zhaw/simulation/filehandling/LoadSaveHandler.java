package ch.zhaw.simulation.filehandling;

import java.io.File;

import javax.swing.JFrame;

import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.plugin.PluginDescription;
import ch.zhaw.simulation.inexport.ImportPlugin;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.status.StatusHandler;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class LoadSaveHandler extends StatusHandler {
	private File path;
	private SimzSaver saver = new SimzSaver();
	private SimzLoader loader = new SimzLoader();
	private Settings settings;
	private JFrame parent;
	private Sysintegration sys;
	private ImportPluginLoader importPluginLoader;

	private static final String LAST_SAVEPATH = "opensave.lastpath";

	public LoadSaveHandler(JFrame parent, Settings settings, Sysintegration sys, ImportPluginLoader importPluginLoader) {
		this.settings = settings;
		this.parent = parent;
		this.sys = sys;
		this.importPluginLoader = importPluginLoader;

		if (settings == null) {
			throw new NullPointerException("settings == null");
		}
		if (sys == null) {
			throw new NullPointerException("sys == null");
		}
		if (importPluginLoader == null) {
			throw new NullPointerException("importPluginLoader == null");
		}
	}

	private String getLastSavePath() {
		return settings.getSetting(LAST_SAVEPATH, System.getProperty("user.home"));
	}

	private void setLastSavePath(File f) {
		settings.setSetting(LAST_SAVEPATH, f.getParentFile().getAbsolutePath());
	}

	public boolean save(SimulationDocument doc) {
		if (path == null || !path.getName().endsWith(".simz")) {
			return saveAs(doc);
		} else {
			if (!checkCanSave(path, doc)) {
				return false;
			}
			if (!doSave(path, doc)) {
				errorSaveFile(doc);
				return false;
			} else {
				setStatusText("Datei gespeichert");
				return true;
			}
		}
	}

	private void errorSaveFile(SimulationDocument doc) {
		Messagebox msg = new Messagebox(parent, "Fehler beim Speichern",
				"<html>Die Datei konnte nicht gespeichert werden!<br>Versuchen Sie die Datei an einen anderen Ort zu speichern.</html>", Messagebox.ERROR);
		msg.addButton("Speichern untern", 0);
		msg.addButton("OK", 1, true);
		if (msg.display() == 0) {
			saveAs(doc);
		}
	}

	private boolean checkCanSave(File file, SimulationDocument doc) {
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
			Messagebox msg = new Messagebox(parent, "Schreibgeschützt", "Die Datei \"" + file.getAbsolutePath() + "\" ist schreibgeschützt.",
					Messagebox.WARNING);
			msg.addButton("Anderswo speichern", 0);
			msg.addButton("Abbrechen", 1);

			if (msg.display() == 0) {
				saveAs(doc);
			}
			return false;
		}
		return true;
	}

	private boolean doSave(File file, SimulationDocument doc) {
		try {
			saver.save(file, doc);
			doc.setSaved();
			return true;
		} catch (Exception e) {
			Errorhandler.logError(e, "Save failed");
			return false;
		}
	}

	public boolean saveAs(SimulationDocument doc) {
		File f = sys.showSaveDialog(parent, simulationXMLSave, getLastSavePath());
		if (f != null) {
			setLastSavePath(f);

			if (!doSave(f, doc)) {
				errorSaveFile(doc);
				return false;
			} else {
				setStatusText("Datei gespeichert");
				path = f;
				return true;
			}
		}
		return false;
	}

	public boolean open(SimulationDocument doc) {
		File f = sys.showOpenDialog(parent, importPluginLoader.getSimulationFileOpen(), getLastSavePath());

		if (f != null) {
			if (!f.isFile()) {
				Messagebox.showError(parent, "Öffnen fehlgeschlagen", "Dies ist keine Datei!");
				return false;
			}
			return open(f, doc);
		}
		return true;
	}

	public boolean open(File file, SimulationDocument doc) {
		if (!file.canRead()) {
			Messagebox msg = new Messagebox(parent, "Fehler beim Öffnen", "Die Datei \"" + file.getAbsolutePath()
					+ "\" kann nicht gelesen werden, keine Rechte!", Messagebox.ERROR);
			msg.addButton("andere Datei öffnen", 0);
			msg.addButton("Abbrechen", 1, true);
			if (msg.display() == 0) {
				return open(doc);
			}
			return false;
		}

		// /////////////////////////
		// import other files
		// /////////////////////////

		for (PluginDescription<ImportPlugin> pluginDescription : importPluginLoader.getPluginDescriptions()) {
			ImportPlugin plugin = pluginDescription.getPlugin();

			try {
				if (plugin.canHandle(file)) {
					plugin.read(file);
					doc.stopAutoparser();

					doc.setType(SimulationType.FLOW_SIMULATION);

					if (plugin.load(doc.getFlowModel())) {
						path = file;
						setStatusText("Datei importiert");
						doc.setSaved();
						setLastSavePath(file);

						return true;
					} else {
						Messagebox msg = new Messagebox(parent, "Fehler beim Import",
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
				doc.startAutoparser();
			}
		}

		// /////////////////////////
		// end import
		// /////////////////////////

		try {
			loader.open(file);
		} catch (Exception e) {
			Messagebox msg = new Messagebox(parent, "Fehler beim Öffnen", "Die Datei \"" + file.getAbsolutePath() + "\" konnte nicht gelesen werden!\n"
					+ e.getMessage(), Messagebox.ERROR);
			msg.addButton("OK", 0, true);
			msg.display();
			return false;
		}

		if (!loader.versionCompatible()) {
			Messagebox msg = new Messagebox(parent, "Fehler beim Öffnen",
					"Die Datei ist von einer neuen Vesion des Programms erstellt worden und kann nicht gelesen werden!", Messagebox.ERROR);
			msg.addButton("OK", 0, true);
			msg.display();
			return false;
		}

		if (!loader.versionOk()) {
			Messagebox msg = new Messagebox(
					parent,
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
			doc.stopAutoparser();

			if (!loader.load(doc)) {
				Messagebox.showWarning(parent, "Probleme beim lesen", "Die Datei enthielt Fehler und konnte möglicherweise nicht korrekt eingelesen werden.");
			}
			path = file;
			setStatusText("Datei geladen");
			doc.setSaved();
		} catch (Exception e) {
			Messagebox msg = new Messagebox(parent, "Fehler beim Öffnen",
					"Die Datei konnte nicht gelesen werden. Ggf. ist die Datei beschädigt oder vom falschen Format.\n" + e.getMessage(), Messagebox.ERROR);
			msg.addButton("OK", 0, true);
			msg.display();

			return false;
		} finally {
			doc.startAutoparser();
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
