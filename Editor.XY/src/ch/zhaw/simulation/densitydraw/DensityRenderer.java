package ch.zhaw.simulation.densitydraw;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public abstract class DensityRenderer {
	private Dimension size;

	public DensityRenderer() {
	}

	public DensityRenderer(Dimension size) {
		setSize(size);
	}

	public void setSize(Dimension size) {
		if (size == null) {
			throw new NullPointerException("size == null");
		}
		this.size = size;
	}

	public void setSize(int width, int height) {
		this.size = new Dimension(width, height);
	}

	public Dimension getSize() {
		return size;
	}

	protected abstract boolean isLogarithmic();

	protected abstract double getMaxMinus();

	protected abstract double getMaxPlus();

	protected abstract double getValueFor(int x, int y) throws Exception;

	/**
	 * 
	 * @param imgToUse
	 *            Recycle an old image, or <code>null</code> to create a new one
	 * @return
	 * @throws Exception
	 */
	public BufferedImage drawDensity(BufferedImage imgToUse) throws Exception {
		BufferedImage img;
		if (imgToUse != null && imgToUse.getWidth() == size.width && imgToUse.getHeight() == size.height) {
			img = imgToUse;
		} else {
			img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		}

		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

		boolean log = isLogarithmic();

		double value;
		double maxMinusFactor;
		double maxPlusFactor;
		if (log) {
			maxMinusFactor = 255.0 / Math.log(getMaxMinus());
			maxPlusFactor = 255.0 / Math.log(getMaxPlus());
		} else {
			maxMinusFactor = 255.0 / getMaxMinus();
			maxPlusFactor = 255.0 / getMaxPlus();
		}

		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++) {
				value = getValueFor(x, y);

				if (log) {
					value = Math.log(value);
				}

				int rgb = 0xffffff;

				if (value < -0.1) {
					int tmp = 255 - (int) (value * maxMinusFactor);
					rgb = (tmp << 16) | (tmp << 8) | 0x0000ff;
				} else if (value > 0.1) {
					int tmp = 255 - ((int) (value * maxPlusFactor));
					rgb = (tmp << 8) | tmp | 0xff0000;
				}

				pixels[x + y * size.width] = rgb;
			}
		}

		return img;
	}
}
