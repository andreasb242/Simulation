package ch.zhaw.simulation.diagram;

import ch.zhaw.simulation.plugin.data.SimulationCollection;

public class DiagramConfigModel {

	private SimulationCollection collection;

	public DiagramConfigModel(SimulationCollection collection) {
		this.collection = collection;
		if (collection == null) {
			throw new NullPointerException("collection == null");
		}
	}

}
