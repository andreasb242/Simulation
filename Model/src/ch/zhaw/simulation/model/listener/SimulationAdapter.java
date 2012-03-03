package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.element.SimulationData;

public abstract class SimulationAdapter implements SimulationListener {

	@Override
	public void dataAdded(SimulationData o) {
	}

	@Override
	public void dataRemoved(SimulationData o) {
	}

	@Override
	public void dataChanged(SimulationData o) {
	}

	@Override
	public void dataSaved(boolean saved) {
	}

	@Override
	public void clearData() {
	}
}
