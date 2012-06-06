package ch.zhaw.simulation.icon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
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

			// Try to convert SVG with Inkscape
			img = convertSvgToPng(file, size);

			if (img == null) {
				// Default red image

				BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
				Graphics g = bi.getGraphics();

				g.setColor(Color.RED);

				g.fillRect(0, 0, size, size);
				g.dispose();
				img = new ImageIcon(bi);

			}
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
	 * @return
	 */
	private static ImageIcon convertSvgToPng(String file, int size) {
		URL loc = IconLoader.class.getProtectionDomain().getCodeSource().getLocation();

		String path = loc.getPath();

		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		if (path.endsWith(".jar") || !path.endsWith("/bin")) {
			System.err.println("Could not convert missing image file: «" + file + "», size = " + size);
			return null;
		}

		// remove /bin and add src for Eclipse
		path = path.substring(0, path.length() - 3);

		String svgFolder = path + "src/ch/zhaw/simulation/icon/svg/";
		String pngFolder = path + "src/ch/zhaw/simulation/icon/png/";

		System.err.println("converting image...");

		File target = new File(pngFolder + size + "/" + file + ".png").getParentFile();
		System.err.println("target folder: " + target.getAbsolutePath());
		if (!target.exists()) {
			System.out.println("creating folder «" + target.getAbsolutePath() + "»");
			if (!target.mkdirs()) {
				System.err.println("could not create folder: «" + target.getAbsolutePath() + "»");
			}
		}

		File sourceFile = new File(svgFolder + file + ".svg");

		if (!sourceFile.exists()) {
			System.err.println("Image file «" + sourceFile.getAbsolutePath() + "» missing!");
			return null;
		}

		String pngImageFile = pngFolder + size + "/" + file + ".png";

		String[] cmdarray = new String[] { "inkscape", "--export-width", "" + size, "--export-height", "" + size, "--export-png", pngImageFile,
				sourceFile.getAbsolutePath() };
		try {
			Process proc = Runtime.getRuntime().exec(cmdarray);
			int returnCode = proc.waitFor();

			if (returnCode != 0) {
				System.err.println("Could not convert missing image, inkscape returned error code: " + returnCode);
			} else {
				return new ImageIcon(ImageIO.read(new File(pngImageFile)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
