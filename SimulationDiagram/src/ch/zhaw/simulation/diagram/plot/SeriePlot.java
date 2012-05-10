package ch.zhaw.simulation.diagram.plot;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Vector;

import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class SeriePlot {

	private int xOrigin = 0;
	private int yOrigin = 0;

	public void plot(Graphics2D g, SimulationSerie serie) {
		g.setColor(serie.getColor());

		Path2D path = new Path2D.Double();

		Vector<SimulationEntry> entries = serie.getData();
		boolean first = true;

		// iterate over entries
		for (SimulationEntry entry : entries) {
			// scale to fit
			double x = entry.time;
			double y = entry.value;

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
