package ch.zhaw.simulation.diagram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramPlot extends JComponent {
	private static final long serialVersionUID = 1L;

	// Because path could reach upper bound and bound covers path => bound displacement
	private static final int BOUND_DISPLACEMENT = 1;

	// Label format
	private static final NumberFormat X_FORMAT = new DecimalFormat("0.0");
	private static final NumberFormat Y_FORMAT = new DecimalFormat("###E0");

	// Range is set static (over GUI or zoom) or auto (default)
	// TODO: not implemented
	public enum RangeType {
		STATIC,
		AUTO
	};

	private RangeType xRangeType;
	private double xRangeMin;
	private double xRangeMax;
	private double xStep;
	private int xNumLines;

	private RangeType yRangeType;
	private double yRangeMin;
	private double yRangeMax;
	private double yStep;
	private int yNumLines;

	// Black markers at the beginning and end of a grid-line
	private int markerLength;

	// Selected path
	private SimulationCollection collection;

	public DiagramPlot(DiagramConfigModel model) {
		// Size of Scrollbar
		setPreferredSize(new Dimension(200, 200));

		xRangeType = RangeType.AUTO;
		yRangeType = RangeType.AUTO;
		xNumLines = 10;
		yNumLines = 10;
		markerLength = 4;
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g;

		g = DrawHelper.antialisingOn(g1);

		int w = getWidth();
		int h = getHeight();

		// Background white
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, w, h);

		paintPlot(g);
	}

	private void paintPlot(Graphics2D g) {
		SimulationSerie serie;
		Vector<SimulationEntry> entries;
		SimulationEntry entry;
		Path2D path;
		FontMetrics fm = g.getFontMetrics();
		int i, k;
		double diffX, diffY;
		double diffYRound;
		double x, y;
		double ratioX, ratioY;
		boolean first;
		double step;
		double stepCount;
		String label;

		// DEBUG
		//NumberFormat f = new DecimalFormat("0.000");

		int w = getWidth() - 75;
		int h = getHeight() - 75;
		int xOrigin = 50;
		int yOrigin = 25;

		//g.setColor(Color.BLACK);
		//g.drawLine(0, 0, xOrigin, yOrigin);

		// Fill backgrund with white
		g.setColor(Color.WHITE);
		g.fillRect(xOrigin, yOrigin - BOUND_DISPLACEMENT, w, h + BOUND_DISPLACEMENT);

		//
		if (collection != null && collection.size() > 0) {

			// Set time-ratio (x-axis)
			diffX  = xRangeMax - xRangeMin;

			ratioX = diffX / (double) w;

			// Set y-ratio (y-axis)
			diffY      = yRangeMax - yRangeMin;
			diffYRound = round(diffY);
			ratioY     = diffYRound / (double) h;

			// DEBUG
			// System.out.println("--- ratio " + f.format(ratioX) + "/" + f.format(ratioY) + ") ---");
			// System.out.println("--- max " + f.format((collection.getEndTime() - collection.getStartTime())) + "/" + f.format(round(collection.getYMax() - collection.getYMin())) + " ---");

			// X
			// From zero point off
			step = step(diffX, xNumLines);
			stepCount = xRangeMin;
			for (i = 0; i <= xNumLines; i++) {
				// number from file to display-number
				x = stepCount / ratioX;

				g.setColor(Color.LIGHT_GRAY);
				//         X1                 Y1                                X2                 Y2
				g.drawLine(xOrigin + (int) x, yOrigin,                          xOrigin + (int) x, yOrigin + (int) h);

				g.setColor(Color.BLACK);
				//         X1                 Y1                                X2                 Y2
				g.drawLine(xOrigin + (int) x, yOrigin, xOrigin + (int) x, yOrigin + markerLength);
				g.drawLine(xOrigin + (int) x, yOrigin + (int) h - markerLength, xOrigin + (int) x, yOrigin + (int) h);

				g.drawString(X_FORMAT.format(stepCount), xOrigin + (int) x - 7, yOrigin + (int) h + 15);
				stepCount += step;
			}

			// Y
			// From zero point off
			step = step(diffY, yNumLines);
			stepCount = yRangeMin;
			for (i = 0; i <= yNumLines; i++) {
				// number from file to display-number
				y = (diffYRound - stepCount) / ratioY;

				g.setColor(Color.LIGHT_GRAY);
				//         X1                 Y1                                X2                 Y2
				g.drawLine(xOrigin, yOrigin + (int) y, xOrigin + w, yOrigin + (int) y);

				g.setColor(Color.BLACK);
				//         X1                 Y1                                X2                 Y2
				g.drawLine(xOrigin, yOrigin + (int) y, xOrigin + markerLength, yOrigin + (int) y);
				g.drawLine(xOrigin + w - markerLength, yOrigin + (int) y, xOrigin + w, yOrigin + (int) y);

				label = Y_FORMAT.format(stepCount);
				g.drawString(label, xOrigin - fm.stringWidth(label) - 4, yOrigin + (int) y + 5);
				stepCount += step;
			}

			
			// iterate over all series
			for (i = 0; i < collection.size(); i++) {

				serie = collection.getSerie(i);

				// create a new path and set color
				g.setColor(serie.getColor());
				path = new Path2D.Double();


				entries = serie.getData();
				first = true;

				// iterate over entries
				for (k = 0; k < entries.size(); k++) {
					entry = entries.get(k);

					// scale to fit
					x = entry.time / ratioX;
					y = (diffYRound - entry.value) / ratioY;

					// draw point
					if (first) {
						first = false;
						path.moveTo(xOrigin + x, yOrigin + y);
					} else {
						path.lineTo(xOrigin + x, yOrigin + y);
					}
				}

				g.draw(path);
			}
		}

		g.setColor(Color.BLACK);

		// Black bounds
		// 14-----43
		// |       |
		// 12-----23

		// 1
		g.drawLine(xOrigin,     yOrigin - BOUND_DISPLACEMENT,     xOrigin,     yOrigin + h);
		// 2
		g.drawLine(xOrigin,     yOrigin + h,                xOrigin + w, yOrigin + h);
		// 3
		g.drawLine(xOrigin + w, yOrigin - BOUND_DISPLACEMENT,    xOrigin + w, yOrigin + h);
		// 4
		g.drawLine(xOrigin + w, yOrigin - BOUND_DISPLACEMENT,     xOrigin,     yOrigin - BOUND_DISPLACEMENT);
	}

	/**
	 * Round number to nearest value
	 *
	 * ex. d = 8.9    => res = 9
	 *     d = 0.0072 => res = 0.0080
	 *     d = 0.0046 => res = 0.0050
	 *
	 * @param d original value
	 * @return rounded value
	 */
	private double round(double d) {
		int exp = (int) Math.log10(d);
		double pow = Math.pow(10, exp - 1);
		double res = Math.ceil(d / pow) * pow;

		return res;
	}

	/**
	 *
	 * @param d number from file
	 * @param numLines
	 * @return number rounded
	 */
	private double step(double d, double numLines) {
		double tmp = d / (numLines);
		int exp = (int) Math.log10(tmp);
		double pow = Math.pow(10, exp - 1);

		return Math.ceil(tmp / pow) * pow;
	}

	public void setXRange(double min, double max) {
		xRangeType = RangeType.STATIC;
		xRangeMin = min;
		xRangeMax = max;
	}

	public void setYRange(double min, double max) {
		yRangeType = RangeType.STATIC;
		yRangeMin = min;
		yRangeMax = max;
	}

	public void updateSimulationCollection(SimulationCollection collection) {
		this.collection = collection;
		xRangeMin = collection.getStartTime();
		xRangeMax = collection.getEndTime();
		yRangeMin = collection.getYMin();
		yRangeMax = collection.getYMax();

		// if y-min is almost zero or difference (ex. constant value) is small
		if (yRangeMin < 1.0 || (yRangeMax - yRangeMin) < 1.0) {
			yRangeMin = 0.0;
		}
		repaint();
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
