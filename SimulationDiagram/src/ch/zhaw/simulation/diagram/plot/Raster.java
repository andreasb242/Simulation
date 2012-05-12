package ch.zhaw.simulation.diagram.plot;

import java.awt.Graphics2D;

import ch.zhaw.simulation.diagram.DiagramConfiguration;

public class Raster {
	private ZoomAndPositionHandler zoom;
	private double xRangeMin;
	private double xRangeMax;
	private double yRangeMin;
	private double yRangeMax;

	private AxisCalcalator axisX = new AxisCalcalator();
	private AxisCalcalator axisY = new AxisCalcalator();
	private DiagramConfiguration config;

	public Raster(ZoomAndPositionHandler zoom, DiagramConfiguration config) {
		this.zoom = zoom;
		this.config = config;
	}

	public void paint(Graphics2D g) {
		int offsetY = zoom.getOffsetY();
		int offsetX = zoom.getOffsetX();
		double zoomX = zoom.getZoomX() / 100.0;
		double zoomY = zoom.getZoomY() / 100.0;
		int borderPadding = zoom.getBorderPadding();

		int w = Math.abs((int) ((xRangeMax - xRangeMin) * zoomX)) + borderPadding * 2;
		int h = Math.abs((int) ((yRangeMax - yRangeMin) * zoomY)) + borderPadding * 2;

		// Background color
		g.setColor(config.getBackgroundColor());
		g.fillRect(offsetX, offsetY, w, h);

		System.out.println("w=" + w + " / h=" + h);
		System.out.println("xRangeMax=" + xRangeMax + " / xRangeMin=" + xRangeMin);

		g.setColor(config.getBorderColor());
		drawBorder(offsetX, offsetY, zoomX, zoomY, w, h, g);

		drawHorizontalLines(g);

	}

	private void drawHorizontalLines(Graphics2D g) {
		int offsetY = zoom.getOffsetY();
		int offsetX = zoom.getOffsetX();

		g.setColor(config.getRasterColor());

		int axisInterval = axisY.calculateAxisInterval(zoom.getZoomY());

		System.out.println("axisInterval=" + axisInterval);
		int x1 = offsetX;
		int x2 = offsetX + 100;

		for (int i = 0; i < 100; i += axisInterval) {
			int y = i + offsetY;
			g.drawLine(x1, y, x2, y);
		}
	}

	private void drawBorder(int xOffset, int yOffset, double zoomX, double zoomY, int w, int h, Graphics2D g) {
		final int BOUND_DISPLACEMENT = 1;

		// Black bounds
		// 1 4-----4 3
		// | |
		// 1 2-----2 3

		// 1
		g.drawLine(xOffset, yOffset - BOUND_DISPLACEMENT, xOffset, yOffset + h);
		// 2
		g.drawLine(xOffset, yOffset + h, xOffset + w, yOffset + h);
		// 3
		g.drawLine(xOffset + w, yOffset - BOUND_DISPLACEMENT, xOffset + w, yOffset + h);
		// 4
		g.drawLine(xOffset + w, yOffset - BOUND_DISPLACEMENT, xOffset, yOffset - BOUND_DISPLACEMENT);
	}

	public void size(double xRangeMin, double xRangeMax, double yRangeMin, double yRangeMax) {
		this.xRangeMin = xRangeMin;
		this.xRangeMax = xRangeMax;
		this.yRangeMin = yRangeMin;
		this.yRangeMax = yRangeMax;

		// TODO 40 sollte definierbar sein!
		axisX.setRange(xRangeMin, xRangeMax, 40);
		axisY.setRange(yRangeMin, yRangeMax, 40);

		System.out.println("size(" + xRangeMin + ", " + xRangeMax + ", " + yRangeMin + ", " + yRangeMax + ")");
	}

}
