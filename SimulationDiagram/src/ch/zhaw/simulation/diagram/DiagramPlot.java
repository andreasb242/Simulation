package ch.zhaw.simulation.diagram;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.model.xy.ColorCalculator;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramPlot extends JComponent {
	private static final long serialVersionUID = 1L;

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

	private SimulationCollection collection;

	public DiagramPlot() {
		// Grösse für Scrollbar
		setPreferredSize(new Dimension(200, 200));

		xRangeType = RangeType.AUTO;
		yRangeType = RangeType.AUTO;
		xNumLines = 10;
		yNumLines = 10;
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


		Image image = createImage(getWidth() - 75, getHeight() - 75);
		paintPlot(image);
		g.drawImage(image, 50, 25, null);
		/*
		*/
	}

	private void paintPlot(Image image) {
		SimulationSerie serie;
		Vector<SimulationEntry> entries;
		SimulationEntry entry;
		Color colors[];
		Path2D path;
		int i, k;
		double x, y;
		double ratioX, ratioY;
		boolean first;

		Graphics2D g = DrawHelper.antialisingOn(image.getGraphics());
		int w = image.getWidth(null);
		int h = image.getHeight(null);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.BLACK);
		g.drawLine(0, 0, 0, h-1);
		g.drawLine(0, h-1, w-1, h-1);
		g.drawLine(w-1, 0, w-1, h);
		g.drawLine(w-1, 0, 0, 0);

		if (collection != null) {


			// Set the time ratio
			ratioX = (collection.getEndTime() - collection.getStartTime()) / (double) w;

			// Set Y ratio
			ratioY = round(collection.getYMax() - collection.getYMin()) / (double) h;

			System.out.println("--- ratio " + ratioX + "/" + ratioY + " ---");
			System.out.println("--- ymax " + round(collection.getYMax() - collection.getYMin()) + " ---");

			double step = step(collection.getEndTime() - collection.getStartTime(), yNumLines);
			double stepCount = step;
			g.setColor(Color.GRAY);
			for (i = 0; i < yNumLines; i++) {
				y = (round(collection.getYMax() - collection.getYMin()) - stepCount) / ratioY;
				stepCount += step;
				g.drawLine(0, (int)y, w, (int)y);
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
					y = (round(collection.getYMax() - collection.getYMin()) - entry.value) / ratioY;
					System.out.println(" (" + entry.time + "/" + entry.value + ") => (" + x + "/" + y + ")");

					// draw point
					if (first) {
						first = false;
						path.moveTo(x, y);
					} else {
						path.lineTo(x, y);
					}
				}

				g.draw(path);
			}
		}
	}
	
	private double round(double d) {
		int exp = (int) Math.log10(d);
		double pow = Math.pow(10, exp - 1);
		double res = Math.ceil(d / pow) * pow;
		System.out.println("round: " + d + " => " + res);
		return res;
	}

	private double step(double d, double numLines) {
		double tmp = d / numLines;
		int exp = (int) Math.log10(tmp);
		double pow = Math.pow(10, exp - 1);
		return Math.floor(tmp / pow) * pow;
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
		repaint();
	}

}
