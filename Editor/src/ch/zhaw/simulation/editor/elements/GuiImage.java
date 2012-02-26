package ch.zhaw.simulation.editor.elements;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import ch.zhaw.simulation.sysintegration.GuiConfig;

import butti.javalibs.errorhandler.Errorhandler;

/**
 * A painted image
 * 
 * @author Andreas Butti
 */
public abstract class GuiImage {
	/**
	 * The Image cache
	 */
	private static HashMap<Class<?>, HashMap<String, BufferedImage>> cache = new HashMap<Class<?>, HashMap<String, BufferedImage>>();

	/**
	 * The not selected image
	 */
	protected BufferedImage image;

	/**
	 * The Shadow Image
	 */
	private BufferedImage shadow = null;

	/**
	 * The image if the component is selected
	 */
	protected BufferedImage imageSelected;

	/**
	 * The configuration
	 */
	protected GuiConfig config;

	/**
	 * The Width
	 */
	private int width;

	/**
	 * The Height
	 */
	private int height;

	/**
	 * Creates an abstract image
	 * 
	 * @param width
	 *            The width
	 * @param height
	 *            The height
	 * @param config
	 *            The Guiconfiguration
	 */
	public GuiImage(int width, int height, GuiConfig config, boolean hasShadow) {
		this(width, height, config, hasShadow, true);
	}

	/**
	 * Creates an abstract image
	 * 
	 * @param width
	 *            The width
	 * @param height
	 *            The height
	 * @param config
	 *            The Guiconfiguration
	 */
	public GuiImage(int width, int height, GuiConfig config, boolean hasShadow, boolean cache) {
		this.config = config;
		this.width = width;
		this.height = height;

		if (cache) {
			image = getImg("image");

			if (image == null) {
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				imageSelected = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				if (hasShadow) {
					shadow = loadShadowImage();
				}

				putImg("image", image);
				putImg("selected", imageSelected);
				putImg("shadow", shadow);

				initImage();
			} else {
				loadImages();
			}
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			imageSelected = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			if (hasShadow) {
				shadow = loadShadowImage();
			}
			initImage();
		}
	}

	protected void initImage() {
		drawBackground((Graphics2D) image.getGraphics(), false);
		drawBackground((Graphics2D) imageSelected.getGraphics(), true);
	}

	protected void loadImages() {
		imageSelected = getImg("selected");
		shadow = getImg("shadow");
	}

	protected abstract void drawBackground(Graphics2D g, boolean selected);

	/**
	 * Gets the image
	 * 
	 * @param selected
	 *            If the component is selected or not
	 */
	public BufferedImage getImage(boolean selected) {
		if (selected) {
			return imageSelected;
		}
		return image;
	}

	private BufferedImage loadShadowImage() {
		try {
			String filename = getClass().getSimpleName();
			
			// because of inheritance call Classloader instead of getClass().getRessourceAsStream()
			InputStream in = getClass().getClassLoader().getResourceAsStream("ch/zhaw/simulation/editor/elements/shadowimage/" + filename + ".png");

			if (in == null) {
				throw new RuntimeException("shadow image not found: " + filename);
			}

			return ImageIO.read(in);
		} catch (IOException e) {
			Errorhandler.showError(e);
		}
		return null;
	}

	public BufferedImage getDependencyImage() {
		return shadow;
	}

	public final Point getDependencyOffset() {
		return new Point(15, 15);
	}

	public BufferedImage getImg(String name) {
		HashMap<String, BufferedImage> list = cache.get(getClass());
		if (list == null) {
			return null;
		}

		String key = name + width + "x" + height;
		return list.get(key);
	}

	public void putImg(String name, BufferedImage img) {
		HashMap<String, BufferedImage> list = cache.get(getClass());
		if (list == null) {
			list = new HashMap<String, BufferedImage>();
			cache.put(getClass(), list);
		}

		String key = name + width + "x" + height;
		list.put(key, img);
	}
}
