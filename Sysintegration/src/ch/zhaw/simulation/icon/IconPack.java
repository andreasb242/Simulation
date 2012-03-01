package ch.zhaw.simulation.icon;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import butti.javalibs.util.StringUtil;

import com.kitfox.svg.app.beans.SVGIcon;

public class IconPack {
	private SVGIcon icon = null;
	private static final JLabel dummyLabel = new JLabel();
	private HashMap<Integer, ImageIcon> rendered = new HashMap<Integer, ImageIcon>();

	private String filename;
	private String cachePath;
	private String cacheName;

	public IconPack(String filename, String cachePath) {
		this.filename = filename;
		this.cachePath = cachePath;

		this.cacheName = StringUtil.replace(filename, "/", "_");
	}

	public SVGIcon getIcon() {
		if (icon == null) {
			icon = loadIcon(filename);
			icon.setScaleToFit(true);
			icon.setAntiAlias(true);
		}
		return icon;
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

	public ImageIcon get(int size) {
		ImageIcon ii = rendered.get(size);
		if (ii != null) {
			return ii;
		}

		String path = null;
		File file = null;
		if (cachePath != null) {
			path = cachePath + File.separator + cacheName + size + ".png";
			file = new File(path);
		}

		Image img = null;
		try {
			if (cachePath != null) {
				if (file.exists()) {
					img = ImageIO.read(file);
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to load cached icon, rendering instead [" + path + "]...");
			e.printStackTrace();
		}

		if (img == null) {
			img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			getIcon().setPreferredSize(new Dimension(size, size));
			getIcon().paintIcon(dummyLabel, img.getGraphics(), 0, 0);

			try {
				if (file != null) {
					ImageIO.write((RenderedImage) img, "PNG", file);
				}
			} catch (Exception e) {
				System.err.println("Failed to write icon cache, don't really matter [" + path + "]...");
				e.printStackTrace();
			}
		}

		ii = new ImageIcon(img);
		rendered.put(size, ii);

		return ii;
	}
}