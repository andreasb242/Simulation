package ch.zhaw.simulation.diagram.plot;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.util.Vector;

import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class SeriePlot implements ActionListener {

	private SimulationSerie serie;
	private Path2D path;

	private Zoom zoom;

	public SeriePlot(SimulationSerie serie, Zoom zoom) {
		this.serie = serie;
		this.zoom = zoom;

		zoom.addListener(this);

		updatePath();
	}

	private void updatePath() {
		this.path = new Path2D.Double();

		Vector<SimulationEntry> entries = serie.getData();
		boolean first = true;

		int yOffset = zoom.getyOffset();
		int xOffset = zoom.getxOffset();
		double zoomX = zoom.getxZoom() / 100.0;
		double zoomY = zoom.getyZoom() / 100.0;

		// iterate over entries
		for (SimulationEntry entry : entries) {
			// scale to fit
			double x = entry.time;
			double y = entry.value;

			// draw point
			if (first) {
				first = false;
				this.path.moveTo((x * zoomX) + xOffset, (y * zoomY) + yOffset);
			} else {
				this.path.lineTo((x * zoomX) + xOffset, (y * zoomY) + yOffset);
			}
		}
	}

	public void plot(Graphics2D g) {
		g.setColor(serie.getColor());
		g.draw(this.path);
	}

	public void dispose() {
		zoom.removeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand() + ": " + e.getID());
		updatePath();
	}
}
