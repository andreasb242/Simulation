package ch.zhaw.simulation.diagram;

import java.util.Vector;

import ch.zhaw.simulation.plugin.data.SimulationCollection;

public class DiagramConfigModel {

	private SimulationCollection collection;

	private Vector<DiagramConfigListener> listener = new Vector<DiagramConfigListener>();

	public DiagramConfigModel(SimulationCollection collection) {
		this.collection = collection;
		if (collection == null) {
			throw new NullPointerException("collection == null");
		}
	}

	public void setLogEnabled(boolean log) {
		for (DiagramConfigListener l : this.listener) {
			l.setLogEnabled(log);
		}
	}

	public void addListener(DiagramConfigListener listener) {
		this.listener.add(listener);
	}

	public void removeListener(DiagramConfigListener listener) {
		this.listener.remove(listener);
	}

}
