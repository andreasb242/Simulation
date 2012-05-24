package ch.zhaw.simulation.densitydraw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;

public class DensityLegendView extends JComponent implements DensityListener {
	private static final long serialVersionUID = 1L;

	private float min;
	private float max;

	private boolean paintLegend = false;

	final int LEGEND_WIDTH = 20;
	final int HORIZONTAL_FONT_POS = 30;

	public DensityLegendView() {
		setPreferredSize(new Dimension(100, 80));
	}

	@Override
	protected void paintComponent(Graphics g1) {
		int h = getHeight() - 1;

		Graphics2D g = DrawHelper.antialisingOn(g1);

		if (paintLegend) {
			// TODO nur min / max anzeigen wenn nötig!

			// draw +
			g.setPaint(new GradientPaint(0, 0, Color.RED, 0, h / 2, Color.WHITE));
			g.fillRect(0, 0, LEGEND_WIDTH, h / 2);

			// draw -
			g.setPaint(new GradientPaint(0, h / 2, Color.WHITE, 0, h, Color.BLUE));
			g.fillRect(0, h / 2, LEGEND_WIDTH, h / 2 + 1);

			g.setColor(Color.BLACK);

			int offset = g.getFontMetrics().getAscent() / 2;

			g.drawString("0", HORIZONTAL_FONT_POS, h/2 - offset / 2);
		}

		// draw border
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, LEGEND_WIDTH, h);

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
	public void dataUpdated(float min, float max) {
		this.min = min;
		this.max = max;

		paintLegend = true;
		repaint();
	}
}
