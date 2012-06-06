package ch.zhaw.simulation.sysintegration.mac;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JDialog;
import javax.swing.JFrame;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationEventlistener.EventType;
import ch.zhaw.simulation.sysintegration.Toolbar;

public class MacSysintegration extends Sysintegration {

	public MacSysintegration() {
		initMacOsX();
	}

	@Override
	protected void initSysMenuShortcuts() {
		this.sysMenuShortcuts = new SysMenuShortcutsMacOSX();
	}

	@Override
	protected void initBookmarks() {
		this.bookmarks = new MacOsXBookmarks();
	}

	@Override
	public Toolbar createToolbar(int defaultIconSize) {
		return new MacOSXToolbar(defaultIconSize);
	}

	private void initMacOsX() {
		try {
			// Generate and register the OSXAdapter, passing it a hash of
			// all the methods we wish to use as delegates for various
			// com.apple.eawt.ApplicationListener methods
			OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("osXquit", (Class[]) null));
			OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("osXabout", (Class[]) null));
			OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("osXpreferences", (Class[]) null));
			OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("osXLoadFile", new Class[] { String.class }));
		} catch (Exception e) {
			Errorhandler.logError(e, "Error while loading the OSXAdapter:");
		}
	}

	public boolean osXquit() {
		try {
			fireEvent(EventType.EXIT, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void osXabout() {
		try {
			fireEvent(EventType.ABOUT, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void osXpreferences() {
		try {
			fireEvent(EventType.PREFERENCES, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void osXLoadFile(String path) {
		try {
			fireEvent(EventType.OPEN, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public File checkFile(String file, Window parent, SimFileFilter filefilter, String lastSavePath) {
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
				f = null;
			}

		}

		return checkCanSave(f, parent, filefilter, lastSavePath);
	}

	public File showSaveDialog(JDialog parent, SimFileFilter filefilter, String lastSavePath) {
		FileDialog dlg = new FileDialog(parent, "Datei speichern", FileDialog.SAVE);

		return showSaveDialog(parent, filefilter, lastSavePath, dlg);
	}

	public File showSaveDialog(JFrame parent, SimFileFilter filefilter, String lastSavePath) {
		FileDialog dlg = new FileDialog(parent, "Datei speichern", FileDialog.SAVE);

		return showSaveDialog(parent, filefilter, lastSavePath, dlg);
	}

	public File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath) {
		if (parent == null) {
			return showSaveDialog((JDialog) null, filefilter, lastSavePath);
		} else if (parent instanceof JFrame) {
			return showSaveDialog((JFrame) parent, filefilter, lastSavePath);
		} else if (parent instanceof JDialog) {
			return showSaveDialog((JDialog) parent, filefilter, lastSavePath);
		} else {
			Errorhandler.logError(new Exception("typeof parent != JFrame && != JDialog"), "parent: " + parent.getClass());
			return showSaveDialog((JDialog) null, filefilter, lastSavePath);
		}
	}

	private File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath, FileDialog dlg) {
		dlg.setDirectory(lastSavePath);
		dlg.setVisible(true);
		String fn = dlg.getFile();
		if (fn == null) {
			return null;
		} else {
			return checkFile(fn, parent, filefilter, lastSavePath);
		}
	}

	private File showOpenDialog(Window parent, String lastSavePath, FileDialog dlg) {
		dlg.setDirectory(lastSavePath);
		dlg.setVisible(true);
		String fn = dlg.getFile();
		if (fn == null) {
			return null;
		} else {
			return new File(fn);
		}
	}

	public File showOpenDialog(Window parent, FileFilter filefilter, String lastSavePath) {
		if (parent == null) {
			return showOpenDialog((JDialog) null, filefilter, lastSavePath);
		} else if (parent instanceof JFrame) {
			return showOpenDialog((JFrame) parent, filefilter, lastSavePath);
		} else if (parent instanceof JDialog) {
			return showOpenDialog((JDialog) parent, filefilter, lastSavePath);
		} else {
			Errorhandler.logError(new Exception("typeof parent != JFrame && != JDialog"), "parent: " + parent.getClass());
			return showOpenDialog((JDialog) null, filefilter, lastSavePath);
		}
	}

	public File showOpenDialog(JFrame parent, String lastSavePath) {
		FileDialog dlg = new FileDialog(parent, "Datei öffnen", FileDialog.LOAD);

		return showOpenDialog(parent, lastSavePath, dlg);
	}

	public File showOpenDialog(JDialog parent, String lastSavePath) {
		FileDialog dlg = new FileDialog(parent, "Datei öffnen", FileDialog.LOAD);

		return showOpenDialog(parent, lastSavePath, dlg);
	}
}
