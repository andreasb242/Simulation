package ch.zhaw.simulation.sysintegration;

import java.awt.Window;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.sysintegration.SysintegrationEventlistener.EventType;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;
import ch.zhaw.simulation.sysintegration.gui.DefaultToolbar;

public class Sysintegration {

	protected Bookmarks bookmarks;
	protected SysMenuShortcuts sysMenuShortcuts;

	private Vector<SysintegrationEventlistener> eventlistener = new Vector<SysintegrationEventlistener>();

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

	public Toolbar createToolbar(int defaultIconSize) {
		return new DefaultToolbar(defaultIconSize);
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

	protected File checkCanSave(File file, Window parent, SimFileFilter filefilter, String lastSavePath) {
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

	public File showOpenDialog(Window parent, FileFilter filefilter, String lastSavePath) {
		JFileChooser chooser = new JFileChooser(lastSavePath);
		chooser.setFileFilter(filefilter);
		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	public void addListener(SysintegrationEventlistener l) {
		eventlistener.add(l);
	}

	public void removeListener(SysintegrationEventlistener l) {
		eventlistener.remove(l);
	}

	protected void fireEvent(EventType type, String param) {
		for (SysintegrationEventlistener l : this.eventlistener) {
			l.sysEvent(type, param);
		}
	}

	public LookAndFeelMenu createLookAndFeelMenu() {
		return new LookAndFeelMenu();
	}

}
