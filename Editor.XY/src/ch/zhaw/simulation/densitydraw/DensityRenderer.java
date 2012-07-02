package ch.zhaw.simulation.densitydraw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.util.ArrowDraw;

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
		clearCache();
	}

	public void setSize(int width, int height) {
		this.size = new Dimension(width, height);
	}

	protected void clearCache() {
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
	public BufferedImage drawDensityColor(BufferedImage imgToUse) throws Exception {
		BufferedImage img = getImg(imgToUse);

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

	public BufferedImage drawArrows(BufferedImage imgToUse, boolean calculateFirst) throws Exception {
		BufferedImage img = getImg(imgToUse);

		final int arrowSize = 40;

		int h = getSize().height;
		int w = getSize().width;

		Graphics2D g = img.createGraphics();
		DrawHelper.antialisingOn(g);

		if (calculateFirst) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, w, h);
		}

		g.setColor(Color.BLACK);

		GradientField field = new GradientField();
		field.calculateGradient(arrowSize, w, h);
		field.calculateArrows();

		ArrowDraw arrowDraw = new ArrowDraw(10);

		for (int x = 0; x < w; x += arrowSize) {
			for (int y = 0; y < h; y += arrowSize) {
				ArrowArguments arrow = field.valueFor(x / arrowSize, y / arrowSize);

				if (arrow == null) {
					continue;
				}

				int ex = (int) (Math.cos(arrow.angle) * arrowSize * arrow.len);
				int ey = (int) (Math.sin(arrow.angle) * arrowSize * arrow.len);

				int sx = x + arrowSize / 2;
				int sy = y + arrowSize / 2;

				g.drawLine(sx, sy, sx + ex, sy + ey);
				arrowDraw.drawArrow(g, sx, sy, sx + ex, sy + ey);
			}
		}

		g.dispose();

		return img;
	}

	private BufferedImage getImg(BufferedImage imgToUse) {
		BufferedImage img;
		if (imgToUse != null && imgToUse.getWidth() == size.width && imgToUse.getHeight() == size.height) {
			img = imgToUse;
		} else {
			img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		}

		return img;
	}

	public class GradientField {
		private double[][] field;
		private ArrowArguments[][] arrows;

		public void calculateGradient(int raster, int width, int height) throws Exception {
			int raster2 = raster / 2;
			int w = width / raster2;
			int h = height / raster2;

			field = new double[w][h];

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					field[x][y] = average(x * raster2, y * raster2, (x + 1) * raster2, (y + 1) * raster2);
				}
			}

			arrows = new ArrowArguments[w / 2][h / 2];
		}

		public void calculateArrows() {
			for (int x = 0; x < arrows.length; x++) {
				for (int y = 0; y < arrows[0].length; y++) {
					arrows[x][y] = calcArrowFor(x, y);
				}
			}

			normalize(arrows);
		}

		private void normalize(ArrowArguments[][] field) {
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;

			for (int x = 0; x < field.length; x++) {
				for (int y = 0; y < field[0].length; y++) {
					ArrowArguments arrow = field[x][y];

					if (arrow == null) {
						continue;
					}

					double v = arrow.len;
					if (v < min) {
						min = v;
					}
					if (v > max) {
						max = v;
					}
				}
			}

			if (max < -min) {
				max = -min;
			}

			for (int x = 0; x < field.length; x++) {
				for (int y = 0; y < field[0].length; y++) {
					ArrowArguments arrow = field[x][y];

					if (arrow != null) {
						arrow.len /= max;
					}
				}
			}
		}

		public ArrowArguments valueFor(int x, int y) {
			return this.arrows[x][y];
		}

		public ArrowArguments calcArrowFor(int x, int y) {

			final double MIN_DIFF = 0.001;

			double a0 = field[x * 2][y * 2]; // top left
			double a1 = field[x * 2 + 1][y * 2]; // top right
			double a2 = field[x * 2][y * 2 + 1]; // bottom left
			double a3 = field[x * 2 + 1][y * 2 + 1]; // bottom right

			double y0 = a0 + a1; // top
			double y1 = a2 + a3; // bottom

			double x0 = a0 + a2; // left
			double x1 = a1 + a3; // right

			double gradX = x1 - x0;
			double gradY = y1 - y0;

			if (Math.abs(x0 - x1) < MIN_DIFF && Math.abs(y0 - y1) < MIN_DIFF) {
				return null;
			}

			return new ArrowArguments(Math.atan2(gradY, gradX), Math.hypot(gradX, gradY));
		}

		private double average(int x0, int y0, int x1, int y1) throws Exception {
			double value = 0;
			for (int x = x0; x < x1; x++) {
				for (int y = y0; y < y1; y++) {
					value += getValueFor(x, y);
				}
			}

			return value;
		}

	}

	private static class ArrowArguments {
		public double angle;
		public double len;

		public ArrowArguments(double angle, double len) {
			this.angle = angle;
			this.len = len;
		}
	}

}
