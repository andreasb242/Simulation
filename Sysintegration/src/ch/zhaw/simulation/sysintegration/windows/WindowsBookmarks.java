package ch.zhaw.simulation.sysintegration.windows;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import ch.zhaw.simulation.sysintegration.bookmarks.Bookmark;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;

public class WindowsBookmarks extends Bookmarks {
	private static final long serialVersionUID = 1L;

	public WindowsBookmarks() {
	}

	@Override
	protected void loadSystemFolders() {
		FileSystemView filesys = FileSystemView.getFileSystemView();

		// Desktop
		File f = filesys.getHomeDirectory();
		Bookmark desktop = new Bookmark(f.getName(), f.getAbsolutePath(), filesys.getSystemIcon(f));
		addElement(desktop);

		// Eigene Dateien / My Documents
		f = filesys.getDefaultDirectory();
		Bookmark home = new Bookmark(f.getName(), f.getAbsolutePath(), filesys.getSystemIcon(f));
		addElement(home);
	}
}
