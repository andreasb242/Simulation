package gui.diagramm;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import simulation.data.SimulationCollection;
import simulation.data.SimulationSerie;

public abstract class DiagrammRenderer {
	protected AffineTransform transform;

	public DiagrammRenderer(AffineTransform transform) {
		this.transform = transform;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}
	
	public void renderChart(Graphics2D g, SimulationCollection model) {
		for (SimulationSerie s : model.getSeries()) {
			if (s.isVisibel()) {
				renderEntry(g, s, model.getStartTime(), model.getEndTime());
			}
		}
	}

	protected abstract void renderEntry(Graphics2D g, SimulationSerie s, double startTime, double endTime);
}
