package ch.zhaw.simulation.icon;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.kitfox.svg.app.beans.SVGIcon;

public class IconSVG {
	/**
	 * Icons zwischenspeichern
	 */
	private static HashMap<String, IconPack> loadedIcons = new HashMap<String, IconPack>();

	public static ImageIcon getIcon(String file) {
		return getIcon(file, 16);
	}

	public static ImageIcon getIcon(String file, int size) {
		IconPack icon = loadedIcons.get(file);
		if (icon == null) {
			icon = new IconPack(file);
			loadedIcons.put(file, icon);
		}

		return icon.get(size);
	}

	public static ImageIcon getIconShadow(String file, int size) {
		return addShadow(getIcon(file, size));
	}

	public static ImageIcon addShadow(Icon icon) {
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Image shadowImage = IconSVG.getIcon("iconShadow", icon.getIconWidth()).getImage();

		img.getGraphics().drawImage(shadowImage, 0, icon.getIconHeight() - shadowImage.getHeight(null), null);
		icon.paintIcon(new JLabel(), img.getGraphics(), 0, 0);

		return new ImageIcon(img);
	}

	private static SVGIcon loadIcon(String file) {
		SVGIcon icon = new SVGIcon();
		URL url = IconSVG.class.getResource("svg/" + file + ".svg");
		if (url == null) {
			throw new RuntimeException("Icon \"" + file + "\" not found!");
		}
		try {
			icon.setSvgURI(url.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return icon;
	}

	static class IconPack implements Serializable {
		private static final long serialVersionUID = 1L;

		transient private SVGIcon icon = null;
		private static final JLabel dummyLabel = new JLabel();
		private HashMap<Integer, ImageIcon> rendered = new HashMap<Integer, ImageIcon>();

		private String filename;

		public IconPack(String filename) {
			this.filename = filename;
		}

		public SVGIcon getIcon() {
			if (icon == null) {
				icon = loadIcon(filename);
				icon.setScaleToFit(true);
				icon.setAntiAlias(true);
			}
			return icon;
		}

		public ImageIcon get(int size) {
			ImageIcon ii = rendered.get(size);
			if (ii != null) {
				return ii;
			}

			BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			getIcon().setPreferredSize(new Dimension(size, size));
			getIcon().paintIcon(dummyLabel, img.getGraphics(), 0, 0);

			ii = new ImageIcon(img);
			rendered.put(size, ii);

			return ii;
		}
	}
}
