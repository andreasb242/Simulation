package ch.zhaw.simulation.sysintegration;

import java.awt.Window;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;
import ch.zhaw.simulation.sysintegration.gui.DefaultToolbar;

public class Sysintegration {

	protected Bookmarks bookmarks;
	protected SysMenuShortcuts sysMenuShortcuts;

	/**
	 * The color and size configuration of gui components
	 */
	protected GuiConfig guiConfig;

	public Sysintegration() {
		initBookmarks();
		initSysMenuShortcuts();
		initGuiConfig();
	}

	protected void initGuiConfig() {
		this.guiConfig = new GuiConfig();
	}

	protected void initBookmarks() {
		this.bookmarks = new Bookmarks();
	}

	protected void initSysMenuShortcuts() {
		this.sysMenuShortcuts = new SysMenuShortcuts();
	}

	public SysMenuShortcuts getMenu() {
		return this.sysMenuShortcuts;
	}

	public Toolbar createToolbar() {
		return new DefaultToolbar();
	}

	public Bookmarks getBookmarks() {
		return bookmarks;
	}
	
	/**
	 * @return The color and size configuration of gui components
	 */
	public GuiConfig getGuiConfig() {
		return guiConfig;
	}

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

	private File checkCanSave(File file, Window parent, SimFileFilter filefilter, String lastSavePath) {
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

	public File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath) {
		JFileChooser chooser = new JFileChooser(lastSavePath);
		chooser.setFileFilter(filefilter);
		if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return checkFile(chooser.getSelectedFile().getAbsolutePath(), parent, filefilter, lastSavePath);
		}
		return null;
	}

	public File showOpenDialog(JFrame parent, FileFilter filefilter, String lastSavePath) {
		JFileChooser chooser = new JFileChooser(lastSavePath);
		chooser.setFileFilter(filefilter);
		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

}
