package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.element.SimulationObject;

public abstract class SimulationAdapter implements SimulationListener {

	@Override
	public void dataAdded(SimulationObject o) {
	}

	@Override
	public void dataRemoved(SimulationObject o) {
	}

	@Override
	public void dataChanged(SimulationObject o) {
	}

	@Override
	public void dataSaved(boolean saved) {
	}

	@Override
	public void clearData() {
	}
}
