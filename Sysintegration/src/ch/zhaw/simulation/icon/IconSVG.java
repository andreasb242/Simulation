package ch.zhaw.simulation.icon;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class IconSVG {
	/**
	 * Icons zwischenspeichern
	 */
	private static IconCache cache = new IconCache();

	public static ImageIcon getIcon(String file) {
		return getIcon(file, 16);
	}

	public static ImageIcon getIcon(String file, int size) {
		return cache.get(file, size);
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
}
