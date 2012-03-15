package ch.zhaw.simulation.editor.elements;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.sysintegration.GuiConfig;

/**
 * A painted image
 * 
 * @author Andreas Butti
 */
public abstract class GuiImage {

	/**
	 * The Shadow Image
	 */
	private BufferedImage shadow = null;

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

		if (hasShadow) {
			shadow = loadShadowImage();
		}
	}

	public abstract void drawImage(Graphics2D g, boolean selected);

	private BufferedImage loadShadowImage() {
		try {
			String filename = getClass().getSimpleName();

			// because of inheritance call Classloader instead of
			// getClass().getRessourceAsStream()
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static BufferedImage drawToImage(GuiImage img, boolean selected) {
		BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		img.drawImage(g, selected);
		g.dispose();
		return image;
	}

	public static BufferedImage drawToImage(GuiImage img) {
		return drawToImage(img, false);
	}
}
