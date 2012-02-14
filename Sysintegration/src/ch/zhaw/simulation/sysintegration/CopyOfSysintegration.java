//package ch.zhaw.simulation.sysintegration;
//
//import gui.control.SimulationControl;
//
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.FileDialog;
//import java.awt.Window;
//import java.io.File;
//
//import javax.swing.BorderFactory;
//import javax.swing.JComponent;
//import javax.swing.JDialog;
//import javax.swing.JFileChooser;
//import javax.swing.JFrame;
//import javax.swing.JRootPane;
//import javax.swing.JSplitPane;
//
//import model.SimulationAdapter;
//
//import org.jdesktop.swingx.util.OS;
//
//import butti.javalibs.gui.messagebox.Messagebox;
//
//import sysintegration.DefaultToolbar;
//import sysintegration.Integration;
//import sysintegration.FileFilter;
//import sysintegration.SysMenuShortcuts;
//import sysintegration.Toolbar;
//import sysintegration.bookmarks.Bookmarks;
//import sysintegration.bookmarks.GtkBookmarks;
//import sysintegration.linux.GTKFileChooser;
//import sysintegration.linux.GtkIntegration;
//import sysintegration.mac.MacOSXToolbar;
//import sysintegration.mac.OSXAdapter;
//import sysintegration.mac.SysMenuShortcutsMacOSX;
//import butti.javalibs.errorhandler.Errorhandler;
//
//public class Sysintegration {
//
//	private SimulationControl control;
//
//	private GTKFileChooser gtkFilechooser = null;
//
//	private boolean macOsX = false;
//	private boolean linux = false;
//
//	private SysMenuShortcuts menu = new SysMenuShortcuts();
//
//	private Integration integration = new Integration();
//
//	private Bookmarks bookmarks = new Bookmarks();
//
//	public Sysintegration(SimulationControl control) {
//		this.control = control;
//
//		if (OS.isMacOSX()) {
//			initMacOsX();
//			macOsX = true;
//		}
//		if (OS.isLinux()) {
//			initLinux();
//			linux = true;
//		}
//	}
//
//	private void initLinux() {
//		if (GTKFileChooser.test()) {
//			gtkFilechooser = new GTKFileChooser();
//		} else {
//			System.out.println("could not init gtk-filechooser");
//		}
//
//		integration = new GtkIntegration();
//		bookmarks = new GtkBookmarks();
//	}
//
//	public void initJComponnent(JComponent c) {
//		integration.initJcomponent(c);
//	}
//
//	public boolean osXquit() {
//		return control.exit();
//	}
//
//	public void osXabout() {
//		control.about();
//	}
//
//	public SysMenuShortcuts getMenu() {
//		return menu;
//	}
//
//	public void osXpreferences() {
//		control.settings();
//	}
//
//	public void osXLoadFile(String path) {
//		control.open(path);
//	}
//
//
//	public boolean isMacOsX() {
//		return macOsX;
//	}
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
//		if (linux) {
//			try {
//				String path = gtkFilechooser.showSaveDialog(lastSavePath, filefilter.getGtkFilter());
//
//				if (path == null) {
//					return null;
//				}
//
//				return checkFile(path, parent, filefilter, lastSavePath);
//			} catch (Exception e) {
//				Errorhandler.logError(e, "Error showing native dialog...");
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
//		if (linux) {
//			try {
//				String path = gtkFilechooser.showOpenDialog(lastSavePath, filefilter.getGtkFilter());
//
//				if (path == null) {
//					return null;
//				}
//
//				return new File(path);
//			} catch (Exception e) {
//				Errorhandler.logError(e, "Error showing native dialog...");
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
//
//	public Toolbar createToolbar() {
//		if (macOsX) {
//			return new MacOSXToolbar();
//		}
//		return new DefaultToolbar();
//	}
//
//	public Bookmarks getBookmarks() {
//		return bookmarks;
//	}
//}
