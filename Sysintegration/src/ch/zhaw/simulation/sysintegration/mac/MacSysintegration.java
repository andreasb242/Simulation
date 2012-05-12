package ch.zhaw.simulation.sysintegration.mac;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;

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

		// TODO mac os x
//		control.getModel().addListener(new SimulationAdapter() {
//
//			@Override
//			public void dataSaved(boolean saved) {
//				control.getParent().getRootPane().putClientProperty("Window.documentModified", saved);
//			}
//		});

	}

	public boolean osXquit() {
		fireEvent(EventType.EXIT, null);
		return false;
	}

	public void osXabout() {
		fireEvent(EventType.ABOUT, null);
	}

	public void osXpreferences() {
		fireEvent(EventType.PREFERENCES, null);
	}

	public void osXLoadFile(String path) {
		fireEvent(EventType.OPEN, path);
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

}
