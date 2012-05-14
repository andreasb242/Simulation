package ch.zhaw.simulation.diagram;

import ch.zhaw.simulation.plugin.data.SimulationSerie;

public interface DiagramConfigListener {

	public enum Direction {
		X("x"), Y("y");

		public final String name;

		private Direction(String name) {
			this.name = name;
		}
	};

	/**
	 * Shows linear or logarithmic scala
	 */
	public void setLogEnabled(Direction direction, boolean log);

	/**
	 * A Serie was enabled
	 */
	public void serieEnabled(SimulationSerie s);

	/**
	 * A Serie was disabled
	 */
	public void serieDisabled(SimulationSerie s);

}
