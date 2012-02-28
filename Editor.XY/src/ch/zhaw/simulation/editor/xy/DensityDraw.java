package ch.zhaw.simulation.editor.xy;

import java.awt.image.BufferedImage;

import org.nfunk.jep.ParseException;

import butti.javalibs.errorhandler.Errorhandler;

import ch.zhaw.simulation.math.Parser;


public class DensityDraw {
	private int width;
	private int height;

	private BufferedImage img;
	
	private Parser parser = new Parser();

	public DensityDraw(int width, int height) {
		this.width = width;
		this.height = height;
		
		parser.addVar("x", 0);
		parser.addVar("y", 0);
		setFormula("x+y");
	}

	public void updateImage() {
		double[][] values = new double[height][width];

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double v = valueFor(x, y);
				min = Math.min(min, v);
				max = Math.max(max, v);
				values[y][x] = v;
			}
		}

		if (img == null) {
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}

		double maxFactor = 255.0 / max;
		double minFactor = 255.0 / min;

		System.out.println("maxFactor = " + maxFactor);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double v = values[y][x];
				int rgb = 0xffffff;

				if (v < -0.1) {
					int tmp = 255 - (int) (v * minFactor);
					rgb = (tmp << 16) | (tmp << 8) | 0x0000ff;
				} else if (v > 0.1) {
					int tmp = 255 - ((int) (v * maxFactor));
					rgb = (tmp << 8) | tmp | 0xff0000;
				}

				img.setRGB(x, y, rgb);
			}
		}

		values = null;
	}

	private double valueFor(int x, int y) {
		try {
			parser.setVar("x", x);
			parser.setVar("y", y);
			return parser.evaluate();
		} catch (ParseException e) {
			Errorhandler.showError(e, "Formel fehler");
		}
		return 0;
	}

	public BufferedImage getImage() {
		return img;
	}

	public void setFormula(String formula) {
		try {
			parser.simplyfy(formula);
		} catch (ParseException e) {
			Errorhandler.showError(e, "Formel fehler");
		}
	}

}
