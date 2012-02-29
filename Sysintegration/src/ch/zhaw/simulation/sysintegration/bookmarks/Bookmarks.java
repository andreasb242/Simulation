package ch.zhaw.simulation.sysintegration.bookmarks;

import javax.swing.DefaultComboBoxModel;

public class Bookmarks extends DefaultComboBoxModel {
	private static final long serialVersionUID = 1L;

	public Bookmarks() {
		initBookmarkIcons();

		loadSystemFolders();

		if (getSize() != 0) {
			Bookmark bm = (Bookmark) getElementAt(getSize() - 1);
			bm.setSeparator(true);
		}

		loadSystemBookmarks();
	}

	protected void initBookmarkIcons() {
	}

	/**
	 * Loads the system folders, e.g. Desktop, Documents, Home...
	 */
	protected void loadSystemFolders() {
	}

	/**
	 * Loads System bookmarks, defined by the user
	 */
	protected void loadSystemBookmarks() {
	}
}
