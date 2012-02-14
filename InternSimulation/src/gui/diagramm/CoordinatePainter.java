package gui.diagramm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.text.DecimalFormat;

import simulation.data.SimulationCollection;
import simulation.data.SimulationSerie;

public class CoordinatePainter {
	private SimulationCollection series;
	private Line2D.Double xLine;
	private Line2D.Double yLine;
	private DiagrammLayout layout;
	private AffineTransform transform;

	/**
	 * The Font used in the CoordSystem.
	 */
	protected Font font = new Font("sans", Font.PLAIN, 10);

	/**
	 * FontRenderContext used througout the CoordSystem
	 */
	protected final FontRenderContext fontRenderContext = new FontRenderContext(null, false, false);

	private DecimalFormat format = new DecimalFormat("0.00;-0.00");

	public CoordinatePainter(SimulationCollection series, AffineTransform transform, DiagrammLayout layout) {
		this.series = series;
		this.layout = layout;
		this.transform = transform;

		calcValues();
	}

	public void calcValues() {
		calcXLine();
		calcYLine();
	}

	private void calcXLine() {
		double y = 0;

		// Minimalwert > 0 => kein Wert unter 0!
		if (series.getYMin() > 0) {
			y = series.getYMin();
			// Maximalwert < 0: kein Wert über 0!
		} else if (series.getYMax() < 0) {
			y = series.getYMax();
		}

		System.out.println("start: " + series.getStartTime() + "; end: " + series.getEndTime());

		Point2D left = transform.transform(new Point2D.Double(series.getStartTime(), y), null);
		Point2D right = transform.transform(new Point2D.Double(series.getEndTime(), y), null);

		xLine = new Line2D.Double(left, right);
	}

	private void calcYLine() {
		double x = 0;

		// Minimalwert > 0 => kein Wert unter 0!
		if (series.getStartTime() > 0) {
			x = series.getYMin();
		} // EndTime < 0 nicht möglich...

		Point2D.Double bottom = (Double) transform.transform(new Point2D.Double(x, series.getYMin()), null);
		Point2D.Double top = (Double) transform.transform(new Point2D.Double(x, series.getYMax()), null);

		yLine = new Line2D.Double(bottom, top);
	}

	public void paint(Graphics2D g) {
		g.setColor(layout.getCoordinateColor());

		if (xLine == null) {
			// TODO: low low prio: darf eigentlich nicht auftreten
			calcValues();
		}

		g.draw(xLine);
		g.draw(yLine);

		if (isDataVisible()) {
			drawYAxisTicks(g);
			drawNumericalXAxisTicks(g);
		}
	}

	private boolean isDataVisible() {
		for (SimulationSerie s : series.getSeries()) {
			if (s.isVisibel()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A double value can be represented like
	 * <code>d = x * 10<sup>exp</sup></code> and this method returns the value
	 * of exp for a double d.
	 * 
	 * @param d
	 *            the double value
	 * @return the exponent of 10
	 */
	public static int exp(double d) {
		int exp = 0;
		boolean positive = (d <= -1 || d >= 1);

		if (java.lang.Double.isInfinite(d) || java.lang.Double.isNaN(d)) {
			return 0;
		}

		while ((d <= -10) || (d >= 10) || ((d > -1) && (d < 1))) {
			if (positive) {
				d /= 10;
				exp++;
			} else {
				d *= 10;
				exp--;
			}
		}

		return exp;
	}

	/**
	 * Calculates the best tick spacing for the rounded minimal and maximal
	 * values.
	 * 
	 * @param min
	 *            the rounded minimal value
	 * @param max
	 *            the rounded maximal value
	 * @return the spacing of ticks on the x-axis.
	 */
	public static double calculateTickSpacing(double min, double max) {
		double spacing = 1.0;

		double diff = max - min;

		int exp = exp(diff);

		exp--;

		spacing = 1.0 * Math.pow(10.0, (double) exp);

		// Currently, only every second tick gets a label, so 20 - 40 ticks are
		// fine.
		// This should be reduced in a loop probably.
		if ((diff / spacing) < 20)
			return 0.5 * spacing;
		else if ((diff / spacing) > 40)
			return 2 * spacing;
		else
			return spacing;
	}

	/**
	 * This method is called by paintDefault to paint the ticks on the x-axis
	 * for numerical x-axis values.
	 * 
	 * @param g
	 *            the Graphics2D context to paint in
	 */
	public void drawNumericalXAxisTicks(Graphics2D g) {
		// TODO: anpassen

		double min = series.getStartTime();
		double max = series.getEndTime();
		double tick = calculateTickSpacing(min, max);
		double ypt = 0;

		if (series.getYMin() > 0)
			ypt = series.getYMin();
		else if (series.getYMax() < 0)
			ypt = series.getYMax();

		Point2D p = new Point2D.Double(0.0, 0.0);
		Point2D v;
		Line2D ticks = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

		boolean paint = false;

		g.setFont(font);

		int marginOffset = 20;

		for (double d = min; d <= max; d += tick) {
			p.setLocation(d, ypt);
			v = transform.transform(p, null);

			ticks.setLine(v.getX(), v.getY() - marginOffset / 2, v.getX(), v.getY() + marginOffset / 2);
			g.draw(ticks);

			if (paint) {
				String sb = format.format(d);
				Rectangle2D r = font.getStringBounds(sb, fontRenderContext);

				g.drawString(sb, (float) (v.getX() - r.getWidth() / 2), (float) (v.getY() + r.getHeight() + marginOffset));
			}
			paint = !paint;
		}
	}

	/**
	 * This method is called by paintDefault to paint the ticks on the y-axis.
	 * 
	 * @param g
	 *            the Graphics2D context in which to draw
	 */
	public void drawYAxisTicks(Graphics2D g) {
		double min = series.getYMin();
		double max = series.getYMax();
		double tick = calculateTickSpacing(min, max);
		double xpt = 0;

		// shift the y-axis according to the max and min x-values
		if (series.getStartTime() > 0) {
			xpt = series.getStartTime();
		}

		Point2D p = new Point2D.Double(0.0, 0.0);
		Point2D v;
		Line2D ticks = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
		boolean paint = false;

		Color backupColor = g.getColor();
		g.setFont(font);

		int marginOffset = 20;

		for (double d = min; d <= max; d += tick) {
			p.setLocation(xpt, d);
			v = transform.transform(p, null);

			ticks.setLine(v.getX() - marginOffset / 2, v.getY(), v.getX() + marginOffset / 2, v.getY());

			g.draw(ticks);

			if (d != min) {
				ticks.setLine(v.getX() + marginOffset / 2, v.getY(), xLine.getX2(), v.getY());
				g.setColor(Color.lightGray);
				g.draw(ticks);
				g.setColor(backupColor);
			}

			if (paint) {
				String sb = format.format(d);
				Rectangle2D r = font.getStringBounds(sb, fontRenderContext);

				g.drawString(sb, (float) (v.getX() - r.getWidth() - marginOffset), (float) (v.getY() + r.getHeight() / 2));
			}
			paint = !paint;
		}
	}
}
