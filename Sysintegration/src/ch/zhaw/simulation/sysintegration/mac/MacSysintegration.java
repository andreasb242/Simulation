package ch.zhaw.simulation.sysintegration.mac;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.sysintegration.LookAndFeelMenu;
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
	public LookAndFeelMenu createLookAndFeelMenu() {
		/**
		 * On Mac OS X we use always the native look & feel
		 */
		return null;
	}
}
