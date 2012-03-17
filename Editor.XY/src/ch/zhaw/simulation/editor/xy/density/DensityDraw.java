package ch.zhaw.simulation.editor.xy.density;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import org.nfunk.jep.ParseException;

import butti.javalibs.errorhandler.Errorhandler;

import ch.zhaw.simulation.math.Parser;

public class DensityDraw {
	private int width;
	private int height;
	private boolean visible = false;

	private BufferedImage img;
	private BufferedImage imgOther;

	private Parser parser = new Parser();

	private Thread updateThread;
	private boolean cancelUpdate = false;

	public DensityDraw(int width, int height) {
		this.width = width;
		this.height = height;

		parser.addVar("x", 0);
		parser.addVar("y", 0);
		try {
			parser.simplyfy("sin(x/10)");
		} catch (ParseException e) {
			Errorhandler.showError(e, "Formel fehler");
		}
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void updateImageAsynchron(final ActionListener finishListener) {
		synchronized (this) {
			if (updateThread != null) {
				cancelUpdate = true;

				// wait for the last thread finished
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			updateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					BufferedImage img = updateImage();
					if (img != null) {
						synchronized (DensityDraw.this) {
							DensityDraw.this.imgOther = DensityDraw.this.img;
							DensityDraw.this.img = img;
						}

						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								finishListener.actionPerformed(null);
							}
						});
					}

					updateThread = null;
					synchronized (DensityDraw.this) {
						// notify the waiting thread
						DensityDraw.this.notifyAll();
					}
				}
			});
			updateThread.start();
		}
	}

	private BufferedImage updateImage() {
		BufferedImage img;

		double[][] values = new double[height][width];

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width && !cancelUpdate; x++) {
				double v = valueFor(x, y);
				min = Math.min(min, v);
				max = Math.max(max, v);
				values[y][x] = v;
			}
		}

		if (cancelUpdate == true) {
			return null;
		}

		if (imgOther != null && imgOther.getWidth() == width && imgOther.getHeight() == height) {
			img = imgOther;
		} else {
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}

		double maxFactor = 255.0 / max;
		double minFactor = 255.0 / min;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width && !cancelUpdate; x++) {
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

		if (cancelUpdate == true) {
			return null;
		}

		return img;
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
		synchronized (this) {
			return img;
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setFormula(String formula) {
		if (formula == null) {
			// TODO disable parser etc!
			return;
		}
		try {
			System.out.println("-->" + formula);
			parser.simplyfy(formula);
		} catch (ParseException e) {
			Errorhandler.showError(e, "Formel fehler");
		}
	}

}
