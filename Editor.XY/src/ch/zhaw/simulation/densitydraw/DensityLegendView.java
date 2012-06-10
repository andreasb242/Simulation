package ch.zhaw.simulation.densitydraw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;

public class DensityLegendView extends JComponent implements DensityListener {
	private static final long serialVersionUID = 1L;

	private double min;
	private double max;

	private boolean paintLegend = false;

	private DecimalFormat format = new DecimalFormat("0.##E0");

	public DensityLegendView() {
		setPreferredSize(new Dimension(150, 50));
	}

	@Override
	protected void paintComponent(Graphics g1) {
		int w = getWidth() - 1;

		Graphics2D g = DrawHelper.antialisingOn(g1);

		final int CIRCLE_SIZE = 20;

		Shape centerLeft = new Rectangle2D.Double(CIRCLE_SIZE / 2, 0, (w - CIRCLE_SIZE) / 2, CIRCLE_SIZE);
		Shape centerRight = new Rectangle2D.Double(CIRCLE_SIZE / 2 + (w - CIRCLE_SIZE) / 2, 0, (w - CIRCLE_SIZE) / 2, CIRCLE_SIZE);
		Area centerArea = new Area(centerLeft);
		centerArea.add(new Area(centerRight));

		Ellipse2D.Double left = new Ellipse2D.Double(0, 0, CIRCLE_SIZE, CIRCLE_SIZE);
		Ellipse2D.Double right = new Ellipse2D.Double(w - CIRCLE_SIZE, 0, CIRCLE_SIZE, CIRCLE_SIZE);

		if (paintLegend) {
			Area leftArea = new Area(left);
			leftArea.add(new Area(centerLeft));

			g.setPaint(new GradientPaint(0, 0, Color.RED, w / 2, 0, Color.WHITE));
			g.fill(leftArea);

			Area rightArea = new Area(right);
			rightArea.add(new Area(centerRight));

			g.setPaint(new GradientPaint(w / 2, 0, Color.WHITE, w, 0, Color.BLUE));
			g.fill(rightArea);
		}

		Area completeArea = new Area(left);
		completeArea.add(new Area(centerArea));
		completeArea.add(new Area(right));

		g.setColor(Color.GRAY);
		g.draw(completeArea);

		int labelWith = 70;
		int padding = (w - (3 * labelWith)) / 2;

		if (this.paintLegend) {
			paintMark(g, 0, this.max, Color.RED);
			paintMark(g, labelWith + padding + 5, 0, Color.WHITE);
			paintMark(g, 2 * (labelWith + padding), this.min, Color.BLUE);
		}
	}

	private void paintMark(Graphics2D g, int x, double value, Color color) {
		int y = 30;
		int h = 15;

		g.setColor(color);
		g.fillRect(x + 2, y, h, h);
		g.setColor(Color.GRAY);
		g.drawRect(x + 2, y, h, h);

		int ascent = g.getFontMetrics().getAscent();
		int offset = ascent / 2 + 2;

		int ys = h / 2 + y;

		g.setColor(Color.BLACK);
		g.drawString(format.format(value), x + h + 5, ys + offset / 2);
	}

	@Override
	public void noActionPerfomed() {
		// delete legend
		paintLegend = false;
		repaint();
	}

	@Override
	public void actionFailed(Exception reason) {
		noActionPerfomed();
	}

	@Override
	public void dataUpdated(double min, double max) {
		this.min = min;
		this.max = max;

		paintLegend = true;
		repaint();
	}
}
