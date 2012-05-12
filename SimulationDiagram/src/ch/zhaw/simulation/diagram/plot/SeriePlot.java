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

	private ZoomAndPositionHandler zoom;

	public SeriePlot(SimulationSerie serie, ZoomAndPositionHandler zoom) {
		this.serie = serie;
		this.zoom = zoom;

		zoom.addListener(this);

		updatePath();
	}

	private void updatePath() {
		this.path = new Path2D.Double();

		Vector<SimulationEntry> entries = serie.getData();
		boolean first = true;

		int offsetY = zoom.getOffsetY();
		int offsetX = zoom.getOffsetX();
		double zoomX = zoom.getZoomX() / 100.0;
		double zoomY = zoom.getZoomY() / 100.0;
		double dataOffsetY = zoom.getDataOffsetY();
		int borderPadding = zoom.getBorderPadding();

		offsetX += borderPadding;
		offsetY += borderPadding;

		// iterate over entries
		for (SimulationEntry entry : entries) {
			// scale to fit
			double x = entry.time;
			double y = entry.value;

			// draw point
			if (first) {
				first = false;
				this.path.moveTo((x * zoomX) + offsetX, ((-y + dataOffsetY) * zoomY) + offsetY);
			} else {
				this.path.lineTo((x * zoomX) + offsetX, ((-y + dataOffsetY) * zoomY) + offsetY);
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
		updatePath();
	}
}
