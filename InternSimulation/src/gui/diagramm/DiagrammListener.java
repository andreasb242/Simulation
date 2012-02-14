package gui.diagramm;

import simulation.data.SimulationSerie;

public interface DiagrammListener {
	public void visibilityChanged(SimulationSerie serie);
	public void scaleChanged();
}
