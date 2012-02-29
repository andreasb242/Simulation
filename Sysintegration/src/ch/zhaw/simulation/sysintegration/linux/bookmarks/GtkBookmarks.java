package ch.zhaw.simulation.sysintegration.linux.bookmarks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.swing.Icon;

import butti.javalibs.util.StringUtil;

import ch.zhaw.simulation.icon.IconSVG;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmark;
import ch.zhaw.simulation.sysintegration.bookmarks.Bookmarks;

public class GtkBookmarks extends Bookmarks {
	private static final long serialVersionUID = 1L;
	private HashMap<String, Icon> icons;

	public GtkBookmarks() {
	}

	@Override
	protected void initBookmarkIcons() {
		icons = new HashMap<String, Icon>();

		icons.put("XDG_DESKTOP_DIR", IconSVG.getIcon("sys.gtk/desktop", 22));
		icons.put("XDG_DOWNLOAD_DIR", IconSVG.getIcon("sys.gtk/download", 22));
		icons.put("XDG_DOCUMENTS_DIR", IconSVG.getIcon("sys.gtk/documents", 22));
		icons.put("XDG_TEMPLATES_DIR", IconSVG.getIcon("sys.gtk/templates", 22));
		icons.put("XDG_PUBLICSHARE_DIR", IconSVG.getIcon("sys.gtk/share", 22));
		icons.put("XDG_MUSIC_DIR", IconSVG.getIcon("sys.gtk/music", 22));
		icons.put("XDG_PICTURES_DIR", IconSVG.getIcon("sys.gtk/pictures", 22));
		icons.put("XDG_VIDEOS_DIR", IconSVG.getIcon("sys.gtk/video", 22));
		icons.put("default", IconSVG.getIcon("sys.gtk/folder", 22));
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

			Icon icon = icons.get("default");
			
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("file://")) {
					String path = line.substring(7);
					
					File file = new File(path);
					if(!file.exists()) {
						continue;
					}
					
					String name = file.getName();
					addElement(new Bookmark(name, path, icon));
					
					System.out.println(path);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loadSystemFolders() {
		try {
			String home = System.getProperty("user.home");
			File f = new File(home + "/.config/user-dirs.dirs");
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
				String id = line.substring(0, pos);

				pos = line.indexOf("\"", pos + 1);
				if (pos == -1) {
					continue;
				}

				int closingPos = line.indexOf("\"", pos + 1);

				String value = line.substring(pos, closingPos).trim();
				
				value = StringUtil.replace(value, "$HOME", home);

				String name = new File(value).getName();

				Icon icon = icons.get(id);
				if (icon == null) {
					System.out.println("Gtk: No icon for \"" + id + "\" not defined");
					icon = icons.get("default");
				}
				Bookmark b = new Bookmark(name, value, icon);
//				System.out.println(id + "=" + value);

				addElement(b);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
