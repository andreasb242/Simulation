package ch.zhaw.simulation.icon;

import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;

import butti.javalibs.config.Config;

/**
 * For faster image loading: use cache instead of parsing an svg icon each time
 * 
 * @author Andreas Butti
 */
public class IconCache {
	private String cachePath;

	private static HashMap<String, IconPack> loadedIcons = new HashMap<String, IconPack>();

	public IconCache() {
		cachePath = Config.get("iconCache");

		if (cachePath != null) {
			File file = new File(cachePath);
			if (file.exists()) {
				if (!file.isDirectory()) {
					cachePath = null;
				}
			} else {
				if (!file.mkdirs()) {
					cachePath = null;
				}
			}
		}
	}

	public ImageIcon get(String file, int size) {
		IconPack icon = loadedIcons.get(file);
		if (icon == null) {
			icon = new IconPack(file, cachePath);
			loadedIcons.put(file, icon);
		}
		return icon.get(size);
	}

}
