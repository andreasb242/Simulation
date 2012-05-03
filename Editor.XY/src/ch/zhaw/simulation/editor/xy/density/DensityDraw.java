package ch.zhaw.simulation.editor.xy.density;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.SwingUtilities;

import org.nfunk.jep.ParseException;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.math.Parser;

public class DensityDraw {
	private int width;
	private int height;

	private BufferedImage img;
	private BufferedImage imgOther;

	private boolean noFormula = true;
	private Parser parser = new Parser();

	private Thread updateThread;
	private boolean cancelUpdate = false;

	public DensityDraw(int width, int height) {
		setSize(width, height);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		parser.addVar("x", 0);
		parser.addVar("y", 0);
	}

	public void updateImageAsynchron(final ActionListener finishListener) {
		if (noFormula) {
			finishListener.actionPerformed(new ActionEvent(this, 1, "nothing"));
			return;
		}

		try {
			valueFor(0, 0);
		} catch (Exception e) {
			finishListener.actionPerformed(new ActionEvent(this, 2, "failed"));
			return;
		}

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
					try {
						BufferedImage img = updateImage();
						if (img != null) {
							synchronized (DensityDraw.this) {
								DensityDraw.this.imgOther = DensityDraw.this.img;
								DensityDraw.this.img = img;
							}

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									finishListener.actionPerformed(new ActionEvent(this, 0, "repaint"));
								}
							});
						}

						updateThread = null;
						synchronized (DensityDraw.this) {
							// notify the waiting thread
							DensityDraw.this.notifyAll();

						}
					} catch (Exception e) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								finishListener.actionPerformed(new ActionEvent(this, 2, "failed"));
							}
						});

						System.out.println("DensityDraw: " + e.getMessage());
					}
				}
			});
			updateThread.start();
		}
	}

	private BufferedImage updateImage() throws ParseException {
		long startTime = System.currentTimeMillis();

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

		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

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

				pixels[x + y * width] = rgb;
				// slower img.setRGB(x, y, rgb);
			}
		}

		values = null;

		if (cancelUpdate == true) {
			return null;
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("->DensityDraw duration: " + estimatedTime + "ms");

		return img;
	}

	private double valueFor(int x, int y) throws ParseException {
		parser.setVar("x", x);
		parser.setVar("y", y);
		return parser.evaluate();
	}

	public BufferedImage getImage() {
		if (noFormula) {
			return null;
		}

		synchronized (this) {
			return img;
		}
	}

	public void setFormula(String formula) {
		if (formula == null || "".equals(formula)) {
			noFormula = true;
			return;
		}

		try {
			parser.simplyfy(formula);
			noFormula = false;
		} catch (ParseException e) {
			Errorhandler.showError(e, "Formel fehler");
		}
	}

}
