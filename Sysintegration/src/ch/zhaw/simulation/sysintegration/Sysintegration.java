package ch.zhaw.simulation.sysintegration;

import java.awt.Window;
import java.io.File;
import java.util.Vector;

import butti.javalibs.config.Config;
import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.sysintegration.SysintegrationEventlistener.EventType;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;
import ch.zhaw.simulation.sysintegration.filechooser.AbstractFilechooser;
import ch.zhaw.simulation.sysintegration.filechooser.FallbackFilechooser;
import ch.zhaw.simulation.sysintegration.filechooser.Filechooser;
import ch.zhaw.simulation.sysintegration.filechooser.SwingFilechooser;
import ch.zhaw.simulation.sysintegration.gui.DefaultToolbar;

public class Sysintegration {

	protected Bookmarks bookmarks;
	protected SysMenuShortcuts sysMenuShortcuts;
	protected Filechooser filechooser;

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

	protected AbstractFilechooser createFilechooser() {
		return null;
	}

	public Filechooser getFilechooser() {
		SwingFilechooser javaFilechooser = new SwingFilechooser();
		if (Config.is("nativeFilechooserEnabled", true)) {
			if (this.filechooser == null) {
				AbstractFilechooser fc = createFilechooser();

				if (fc == null) {
					this.filechooser = javaFilechooser;
				} else {
					this.filechooser = new FallbackFilechooser(fc, javaFilechooser);
				}
			}
		} else {
			this.filechooser = javaFilechooser;
		}

		return this.filechooser;
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

	public File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath) {
		Filechooser fc = getFilechooser();
		try {
			return fc.showSaveDialog(parent, filefilter, lastSavePath);
		} catch (Exception e) {
			// should not happen, because FallbackFilechooser should handle this
			Errorhandler.showError(e, "Filechooser failed");
			return null;
		}
	}

	public File showOpenDialog(Window parent, SimFileFilter filefilter, String lastSavePath) {
		Filechooser fc = getFilechooser();
		try {
			return fc.showOpenDialog(parent, filefilter, lastSavePath);
		} catch (Exception e) {
			// should not happen, because FallbackFilechooser should handle this
			Errorhandler.showError(e, "Filechooser failed");
			return null;
		}

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
