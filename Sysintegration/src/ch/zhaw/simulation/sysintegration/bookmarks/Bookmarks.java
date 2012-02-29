package ch.zhaw.simulation.sysintegration.bookmarks;

import javax.swing.DefaultComboBoxModel;

import ch.zhaw.simulation.icon.IconSVG;

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

		if (getSize() != 0) {
			Bookmark bm = (Bookmark) getElementAt(getSize() - 1);
			bm.setSeparator(true);
		}

		Bookmark bm = new Bookmark("Anderer", "<custom>", IconSVG.getIcon("custom-folder"));
		addElement(bm);
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
