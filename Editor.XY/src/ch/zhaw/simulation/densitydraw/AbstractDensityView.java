package ch.zhaw.simulation.densitydraw;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.nfunk.jep.ParseException;

/**
 * @author Andreas Butti
 */
public abstract class AbstractDensityView extends DensityRenderer {
	private BufferedImage img;
	private BufferedImage imgOther;

	private Thread updateThread;
	private boolean cancelUpdate = false;

	private double values[][];

	private Vector<DensityListener> listener = new Vector<DensityListener>();
	private double maxMinus;
	private double maxPlus;

	public AbstractDensityView(int width, int height) {
		setSize(width, height);
	}

	public void updateImageAsynchron() {
		try {
			// Check if there are valid data available
			valueFor(0, 0);
		} catch (Exception e) {
			fireActionFailed(e);
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

			cancelUpdate = false;

			updateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {

						final RenderResult result = renderImage();
						if (result != null) {
							synchronized (AbstractDensityView.this) {
								AbstractDensityView.this.imgOther = AbstractDensityView.this.img;
								AbstractDensityView.this.img = result.img;
							}

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									fireDataUpdated(result.min, result.max);
								}
							});
						}

						updateThread = null;
						synchronized (AbstractDensityView.this) {
							// notify the waiting thread
							AbstractDensityView.this.notifyAll();

						}
					} catch (final Exception e) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								fireActionFailed(e);
							}
						});

						System.out.println("DensityDraw: " + e.getMessage());
					}
				}
			});
			updateThread.start();
		}
	}

	@Override
	protected double getMaxMinus() {
		return this.maxMinus;
	}

	@Override
	protected double getMaxPlus() {
		return this.maxPlus;
	}

	protected abstract double valueFor(int x, int y) throws ParseException;

	@Override
	protected final double getValueFor(int x, int y) throws Exception {
		return this.values[y][x];
	}

	private RenderResult renderImage() throws Exception {
		long startTime = System.currentTimeMillis();

		this.maxMinus = Double.MAX_VALUE;
		this.maxPlus = Double.MIN_VALUE;

		int heigth = getSize().height;
		int width = getSize().width;

		if (values == null || values.length != heigth || values[0].length != width) {
			values = new double[heigth][width];
		}

		for (int y = 0; y < heigth; y++) {
			for (int x = 0; x < width && !cancelUpdate; x++) {
				double v = valueFor(x, y);
				if (v < maxMinus) {
					maxMinus = v;
				}
				if (v > maxPlus) {
					maxPlus = v;
				}
				values[y][x] = v;
			}
		}

		if (cancelUpdate == true) {
			return null;
		}

		BufferedImage img = null;
		if (imgOther != null) {
			img = imgOther;
		}

		img = drawDensity(img);

		if (cancelUpdate == true) {
			return null;
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("->DensityDraw duration: " + estimatedTime + "ms");

		return new RenderResult(img, this.maxMinus, this.maxPlus);
	}

	private static class RenderResult {
		BufferedImage img;
		double min;
		double max;

		public RenderResult(BufferedImage img, double min, double max) {
			this.img = img;
			this.min = min;
			this.max = max;
		}
	}

	public synchronized void draw(Graphics2D g, int x, int y, ImageObserver observer) {
		g.drawImage(this.img, x, y, observer);
	}

	/**
	 * Update was starte, but nothing was done
	 */
	protected void fireNoActionPerformed() {
		for (DensityListener l : this.listener) {
			l.noActionPerfomed();
		}
	}

	protected void fireActionFailed(Exception reason) {
		for (DensityListener l : this.listener) {
			l.actionFailed(reason);
		}
	}

	protected void fireDataUpdated(double min, double max) {
		for (DensityListener l : this.listener) {
			l.dataUpdated(min, max);
		}
	}

	public void addListener(DensityListener l) {
		listener.add(l);
	}

	public void removeListener(DensityListener l) {
		listener.remove(l);
	}
}
