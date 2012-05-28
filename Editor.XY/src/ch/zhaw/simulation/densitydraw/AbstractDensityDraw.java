package ch.zhaw.simulation.densitydraw;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.nfunk.jep.ParseException;

// TODO: wird nur ein mal instanziert? wann soll cancelUpdate geresetted (= false) werden?
public abstract class AbstractDensityDraw {
	private int width;
	private int height;

	private BufferedImage img;
	private BufferedImage imgOther;

	private Thread updateThread;
	private boolean cancelUpdate = false;

	private Vector<DensityListener> listener = new Vector<DensityListener>();

	public AbstractDensityDraw(int width, int height) {
		setSize(width, height);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
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

			// TODO: vorschlag von Andreas B. ;)
			cancelUpdate = false;

			updateThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						final RenderResult result = renderImage();
						if (result != null) {
							synchronized (AbstractDensityDraw.this) {
								AbstractDensityDraw.this.imgOther = AbstractDensityDraw.this.img;
								AbstractDensityDraw.this.img = result.img;
							}

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									fireDataUpdated(result.min, result.max);
								}
							});
						}

						updateThread = null;
						synchronized (AbstractDensityDraw.this) {
							// notify the waiting thread
							AbstractDensityDraw.this.notifyAll();

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

	private RenderResult renderImage() throws ParseException {
		long startTime = System.currentTimeMillis();

		BufferedImage img;

		float[][] values = new float[height][width];

		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width && !cancelUpdate; x++) {
				float v = valueFor(x, y);
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
				// slower: img.setRGB(x, y, rgb);
			}
		}

		values = null;

		if (cancelUpdate == true) {
			return null;
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("->DensityDraw duration: " + estimatedTime + "ms");

		return new RenderResult(img, min, max);
	}

	private static class RenderResult {
		BufferedImage img;
		float min;
		float max;

		public RenderResult(BufferedImage img, float min, float max) {
			this.img = img;
			this.min = min;
			this.max = max;
		}
	}

	protected abstract float valueFor(int x, int y) throws ParseException;

	public BufferedImage getImage() {
		synchronized (this) {
			return img;
		}
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

	protected void fireDataUpdated(float min, float max) {
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
