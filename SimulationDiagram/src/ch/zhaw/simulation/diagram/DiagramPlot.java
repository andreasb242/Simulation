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

		//


		if (collection != null) {

			// Set the time ratio
			ratioX = (collection.getEndTime() - collection.getStartTime()) / (double) w;

			// iterate over all series
			for (i = 0; i < collection.size(); i++) {

				serie = collection.getSerie(i);

				// create a new path and set color
				g.setColor(serie.getColor());
				path = new Path2D.Double();

				// Set Y ratio
				ratioY = (serie.getMax() - serie.getMin()) / (double) h;

				entries = serie.getData();
				first = true;

				// iterate over entries
				for (k = 0; k < entries.size(); k++) {
					entry = entries.get(k);

					// scale to fit
					x = entry.time / ratioX;
					y = (serie.getMax() - entry.value) / ratioY;

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
