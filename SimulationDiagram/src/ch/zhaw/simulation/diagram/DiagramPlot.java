package ch.zhaw.simulation.diagram;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Vector;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramPlot extends JComponent {
	private static final long serialVersionUID = 1L;

	public enum RangeType {
		STATIC,
		AUTO
	};

	public enum Strategy {
		NORMAL,
		EXPONENTIAL
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

	private int markerLength;

	private SimulationCollection collection;

	public DiagramPlot() {
		// Grösse für Scrollbar
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
		Color colors[];
		Path2D path;
		FontMetrics fm = g.getFontMetrics();
		int i, k;
		double diffX, diffY;
		double x, y;
		double ratioX, ratioY;
		boolean first;
		double step;
		double stepCount;
		NumberFormat nf = new DecimalFormat("0.0");
		NumberFormat nfExp = new DecimalFormat("###E0");
		String label;
		Strategy strategy;

		// DEBUG
		NumberFormat f = new DecimalFormat("0.000");

		int w = getWidth() - 75;
		int h = getHeight() - 75;
		int xOrigin = 50;
		int yOrigin = 25;

		//g.setColor(Color.BLACK);
		//g.drawLine(0, 0, xOrigin, yOrigin);

		// Fill backgrund with white
		g.setColor(Color.WHITE);
		g.fillRect(xOrigin, yOrigin - 1, w, h + 1);

		//
		if (collection != null && collection.size() > 0) {

			// Set time-ratio (x-axis)
			diffX  = xRangeMax - xRangeMin;
			ratioX = diffX / (double) w;

			// Set y-ratio (y-axis)
			diffY     = yRangeMax - yRangeMin;
			ratioY    = round(diffY) / (double) h;

			System.out.println("--- ratio " + f.format(ratioX) + "/" + f.format(ratioY) + ") ---");
			System.out.println("--- max " + f.format((collection.getEndTime() - collection.getStartTime())) + "/" + f.format(round(collection.getYMax() - collection.getYMin())) + " ---");

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

				g.drawString(nf.format(stepCount), xOrigin + (int) x - 7, yOrigin + (int) h + 15);
				stepCount += step;
			}

			// Y
			// From zero point off
			step = step(diffY, yNumLines);
			stepCount = yRangeMin;
			for (i = 0; i <= yNumLines; i++) {
				// number from file to display-number
				y = (round(diffY) - stepCount) / ratioY;

				g.setColor(Color.LIGHT_GRAY);
				//         X1                 Y1                                X2                 Y2
				g.drawLine(xOrigin, yOrigin + (int) y, xOrigin + w, yOrigin + (int) y);

				g.setColor(Color.BLACK);
				//         X1                 Y1                                X2                 Y2
				g.drawLine(xOrigin, yOrigin + (int) y, xOrigin + markerLength, yOrigin + (int) y);
				g.drawLine(xOrigin + w - markerLength, yOrigin + (int) y, xOrigin + w, yOrigin + (int) y);

				label = nfExp.format(stepCount);
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

				System.out.println("=== " + serie.getName() + " =========================");
				// iterate over entries
				for (k = 0; k < entries.size(); k++) {
					entry = entries.get(k);

					// scale to fit
					x = entry.time / ratioX;
					y = (round(diffY) - entry.value) / ratioY;
					//System.out.println(" (" + entry.time + "/" + entry.value + ") => (" + x + "/" + y + ")");

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
		g.drawLine(xOrigin,     yOrigin - 1,     xOrigin,     yOrigin + h);
		g.drawLine(xOrigin,     yOrigin + h, xOrigin + w, yOrigin + h);
		g.drawLine(xOrigin + w, yOrigin - 1 ,     xOrigin + w, yOrigin + h);
		g.drawLine(xOrigin + w, yOrigin - 1,     xOrigin,     yOrigin - 1);
	}

	private double round(double d) {
		int exp = (int) Math.log10(d);
		double pow = Math.pow(10, exp - 1);
		double res = Math.ceil(d / pow) * pow;
		//System.out.println("round: " + d + " => " + res);
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
		if (yRangeMin < 1.0 || (yRangeMax - yRangeMin) < 1.0) {
			yRangeMin = 0.0;
		}
		repaint();
	}

}
