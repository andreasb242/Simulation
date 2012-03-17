package ch.zhaw.simulation.sysintegration.mac;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

import butti.javalibs.errorhandler.Errorhandler;

import ch.zhaw.simulation.sysintegration.bookmarks.Bookmark;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;

public class MacOsXBookmarks extends Bookmarks {
	private static final long serialVersionUID = 1L;

	private FileSystemView filesys;

	public MacOsXBookmarks() {
	}

	private void addElement(String path) throws IOException {
		File f = new File(path).getCanonicalFile();
		Bookmark home = new Bookmark(f.getName(), f.getAbsolutePath(), filesys.getSystemIcon(f));
		addElement(home);
	}

	@Override
	protected void loadSystemFolders() {
		filesys = FileSystemView.getFileSystemView();

		try {
			// Home
			String path = filesys.getHomeDirectory().getCanonicalPath() + "/";
			addElement(path);
			addElement(path + "Desktop");
			addElement(path + "Downloads");
			addElement(path + "Movies");
			addElement(path + "Pictures");
			addElement(path + "Documents");
			addElement(path + "Music");
			addElement(path + "Public");
		} catch (Exception e) {
			Errorhandler.showError(e);
		}
	}
}
