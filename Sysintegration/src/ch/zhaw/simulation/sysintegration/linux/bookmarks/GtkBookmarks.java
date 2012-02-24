package ch.zhaw.simulation.sysintegration.linux.bookmarks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.swing.Icon;

import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;

public class GtkBookmarks extends Bookmarks {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Icon> icons;

	@Override
	protected void initBookmarkIcons() {
		icons = new HashMap<String, Icon>();

		icons.put("XDG_DESKTOP_DIR", IconSVG.getIcon("sys.gtk/desktop", 22));
		icons.put("XDG_DOWNLOAD_DIR", IconSVG.getIcon("sys.gtk/download", 22));
		icons.put("XDG_TEMPLATES_DIR", IconSVG.getIcon("sys.gtk/templates", 22));
		icons.put("XDG_PUBLICSHARE_DIR", IconSVG.getIcon("sys.gtk/share", 22));
		icons.put("XDG_MUSIC_DIR", IconSVG.getIcon("sys.gtk/music", 22));
		icons.put("XDG_PICTURES_DIR", IconSVG.getIcon("sys.gtk/pictures", 22));
		icons.put("XDG_VIDEOS_DIR", IconSVG.getIcon("sys.gtk/video", 22));
	}

	@Override
	protected void loadSystemBookmarks() {
		try {
			File f = new File(System.getProperty("user.home") + "/.gtk-bookmarks");
			if (!f.exists()) {
				System.err.println("GTK Bookmarks nicht gefunden! (~/.gtk-bookmarks)");
				return;
			}

			FileReader in = new FileReader(f);
			BufferedReader reader = new BufferedReader(in);
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("file://")) {
					// TODO: parsen GTK Bookmarks
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loadSystemFolders() {
		try {
			File f = new File(System.getProperty("user.home") + "/.config/user-dirs.dirs");
			if (!f.exists()) {
				System.err.println("X Folder configfile nicht vorhanden! (~/.config/user-dirs.dirs)");
				return;
			}

			FileReader in = new FileReader(f);
			BufferedReader reader = new BufferedReader(in);
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				int pos = line.indexOf("=");
				if (pos == -1) {
					continue;
				}
				String name = line.substring(0, pos);

				pos = line.indexOf("\"", pos + 1);
				if (pos == -1) {
					continue;
				}

				int closingPos = line.indexOf("\"", pos + 1);

				String value = line.substring(pos, closingPos).trim();

				// TODO: GtkBookmarks
//				System.out.println("val: " + name + " = " + value);
//				System.out.println(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
