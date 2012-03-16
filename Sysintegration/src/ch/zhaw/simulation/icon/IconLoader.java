package ch.zhaw.simulation.icon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class IconLoader {
	/**
	 * Icons zwischenspeichern
	 */
	private static HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

	private static final boolean autoconvertSvgToPng = false;

	public static ImageIcon getIcon(String file) {
		return getIcon(file, 16);
	}

	public static ImageIcon getIcon(String file, int size) {
		String path = String.valueOf(size) + "/" + file + ".png";

		ImageIcon img = icons.get(path);
		if (img != null) {
			return img;
		}

		try {
			img = new ImageIcon(ImageIO.read(IconLoader.class.getResourceAsStream("png/" + path)));
		} catch (Exception e) {
			System.err.println("icon «" + path + "» not found!");

			if (autoconvertSvgToPng) {
				convertSvgToPng(file, size);
			}

			// convert -size 320x85 xc:transparent -font Bookman-DemiItalic

			BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.getGraphics();

			g.setColor(Color.RED);

			g.fillRect(0, 0, size, size);
			g.dispose();
			img = new ImageIcon(bi);
		}

		icons.put(path, img);
		return img;
	}

	/**
	 * This is a method to convert SVG images with Inkscape. Only used while
	 * developing
	 * 
	 * @param file
	 *            The file
	 * @param size
	 *            The size in px
	 */
	private static void convertSvgToPng(String file, int size) {
		String svgFolder = "/home/andreas/git/SimulationBA/Sysintegration/src/ch/zhaw/simulation/icon/svg/";
		String pngFolder = "/home/andreas/git/SimulationBA/Sysintegration/src/ch/zhaw/simulation/icon/png/";

		System.err.println("converting image...");

		String[] cmdarray = new String[] { "inkscape", "--export-width", "" + size, "--export-height", "" + size, "--export-png",
				pngFolder + size + "/" + file + ".png", svgFolder + file + ".svg" };
		try {
			Runtime.getRuntime().exec(cmdarray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ImageIcon getIconShadow(String file, int size) {
		return addShadow(getIcon(file, size));
	}

	public static ImageIcon addShadow(Icon icon) {
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Image shadowImage = IconLoader.getIcon("iconShadow", icon.getIconWidth()).getImage();

		img.getGraphics().drawImage(shadowImage, 0, icon.getIconHeight() - shadowImage.getHeight(null), null);
		icon.paintIcon(new JLabel(), img.getGraphics(), 0, 0);

		return new ImageIcon(img);
	}
}
