package ch.zhaw.simulation.sysintegration.mac;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.sysintegration.SysMenuShortcuts;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;

public class MacSysintegration extends Sysintegration  {

	public MacSysintegration() {
	}
	
	@Override
	protected void initSysMenuShortcuts() {
		this.sysMenuShortcuts = new SysMenuShortcutsMacOSX();
	}
	
	@Override
	public Toolbar createToolbar() {
		return new MacOSXToolbar();
	}
	
	private void initMacOsX() {
		// TODO: mac os x
//		try {
//			// Generate and register the OSXAdapter, passing it a hash of
//			// all the methods we wish to
//			// use as delegates for various
//			// com.apple.eawt.ApplicationListener methods
//			OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("osXquit", (Class[]) null));
//			OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("osXabout", (Class[]) null));
//			OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("osXpreferences", (Class[]) null));
//			OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("osXLoadFile", new Class[] { String.class }));
//		} catch (Exception e) {
//			Errorhandler.logError(e, "Error while loading the OSXAdapter:");
//		}
//
//		control.getModel().addListener(new SimulationAdapter() {
//
//			@Override
//			public void dataSaved(boolean saved) {
//				control.getParent().getRootPane().putClientProperty("Window.documentModified", saved);
//			}
//		});
//
//		JRootPane p = control.getParent().getRootPane();
//
//		for (Component c : p.getComponents()) {
//			System.out.println(c.getClass());
//			if (c instanceof JSplitPane) {
//				JSplitPane split = (JSplitPane) c;
//				((JComponent) split.getLeftComponent()).setBorder(BorderFactory.createLineBorder(Color.RED));
//			}
//		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	// TODO: mac os x
	
	
	
	
//	
//	
//
//	public File showSaveDialog(JDialog parent, FileFilter filefilter, String lastSavePath) {
//		FileDialog dlg = null;
//		if (macOsX) {
//			dlg = new FileDialog(parent, "Datei speichern", FileDialog.SAVE);
//		}
//
//		return showSaveDialog(parent, filefilter, lastSavePath, dlg);
//	}
//
//	public File showSaveDialog(JFrame parent, FileFilter filefilter, String lastSavePath) {
//		FileDialog dlg = null;
//		if (macOsX) {
//			dlg = new FileDialog(parent, "Datei speichern", FileDialog.SAVE);
//		}
//
//		return showSaveDialog(parent, filefilter, lastSavePath, dlg);
//	}
//
//	public File checkFile(String file, Window parent, FileFilter filefilter, String lastSavePath) {
//		File f;
//		if (!file.endsWith(filefilter.getExtension())) {
//			f = new File(file + filefilter.getExtension());
//		} else {
//			f = new File(file);
//		}
//
//		if (f.exists()) {
//			Messagebox msg = new Messagebox(parent, "Überschreiben", "Soll die Datei \"" + f.getAbsolutePath() + "\" überschreiben werden?",
//					Messagebox.QUESTION);
//			msg.addButton("Speichern unter", 0);
//			msg.addButton("Abbrechen", 1);
//			msg.addButton("Überschreiben", 2, true);
//
//			int res = msg.display();
//			if (res == 0) {
//				f = showSaveDialog(parent, filefilter, lastSavePath);
//			} else if (res != 2) {
//				f = null;
//			}
//
//		}
//
//		return checkCanSave(f, parent, filefilter, lastSavePath);
//	}
//
//	private File showSaveDialog(Window parent, FileFilter filefilter, String lastSavePath) {
//		if (parent == null) {
//			return showSaveDialog((JDialog) null, filefilter, lastSavePath);
//		} else if (parent instanceof JFrame) {
//			return showSaveDialog((JFrame) parent, filefilter, lastSavePath);
//		} else if (parent instanceof JDialog) {
//			return showSaveDialog((JDialog) parent, filefilter, lastSavePath);
//		} else {
//			Errorhandler.logError(new Exception("typeof parent != JFrame && != JDialog"), "parent: " + parent.getClass());
//			return showSaveDialog((JDialog) null, filefilter, lastSavePath);
//		}
//	}
//
//	private File checkCanSave(File file, Window parent, FileFilter filefilter, String lastSavePath) {
//		boolean canWrite = true;
//
//		if (!file.exists()) {
//			try {
//				canWrite = file.createNewFile();
//
//				if (canWrite) {
//					canWrite = file.canWrite();
//				}
//			} catch (Exception e) {
//				Errorhandler.logError(e);
//				canWrite = false;
//			}
//		} else {
//			canWrite = file.canWrite();
//		}
//
//		if (!canWrite) {
//			Messagebox msg = new Messagebox(control.getParent(), "Schreibgeschützt", "Die Datei \"" + file.getAbsolutePath() + "\" ist schreibgeschützt.",
//					Messagebox.WARNING);
//			msg.addButton("Anderswo speichern", 0);
//			msg.addButton("Abbrechen", 1);
//
//			if (msg.display() == 0) {
//				return showSaveDialog(parent, filefilter, lastSavePath);
//			}
//			return null;
//		}
//		return file;
//	}
//
//	private File showSaveDialog(Window parent, FileFilter filefilter, String lastSavePath, FileDialog dlg) {
//		if (macOsX) {
//			dlg.setDirectory(lastSavePath);
//			dlg.setVisible(true);
//			String fn = dlg.getFile();
//			if (fn == null) {
//				return null;
//			} else {
//				return checkFile(fn, parent, filefilter, lastSavePath);
//			}
//		}
//
//		JFileChooser chooser = new JFileChooser(lastSavePath);
//		chooser.setFileFilter(filefilter);
//		if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
//			return checkFile(chooser.getSelectedFile().getAbsolutePath(), parent, filefilter, lastSavePath);
//		}
//		return null;
//	}
//
//	public File showOpenDialog(JFrame parent, FileFilter filefilter, String lastSavePath) {
//
//		if (macOsX) {
//			FileDialog dlg = new FileDialog(parent, "Datei öffnen", FileDialog.LOAD);
//			dlg.setDirectory(lastSavePath);
//			dlg.setVisible(true);
//			String fn = dlg.getFile();
//			if (fn == null) {
//				return null;
//			} else {
//				return new File(fn);
//			}
//		}
//
//		JFileChooser chooser = new JFileChooser(lastSavePath);
//		chooser.setFileFilter(filefilter);
//		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
//			return chooser.getSelectedFile();
//		}
//		return null;
//	}

	
}
