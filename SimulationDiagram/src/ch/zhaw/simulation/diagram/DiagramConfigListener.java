package ch.zhaw.simulation.diagram;

import ch.zhaw.simulation.plugin.data.SimulationSerie;

public interface DiagramConfigListener {

	/**
	 * Shows linear or logarithmic scala
	 */
	public void setLogEnabled(boolean log);

	/**
	 * A Serie was enabled
	 */
	public void serieEnabled(SimulationSerie s);

	/**
	 * A Serie was disabled
	 */
	public void serieDisabled(SimulationSerie s);

}