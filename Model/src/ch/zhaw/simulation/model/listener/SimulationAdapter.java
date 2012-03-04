package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.element.AbstractSimulationData;

public abstract class SimulationAdapter implements SimulationListener {

	@Override
	public void dataAdded(AbstractSimulationData o) {
	}

	@Override
	public void dataRemoved(AbstractSimulationData o) {
	}

	@Override
	public void dataChanged(AbstractSimulationData o) {
	}

	@Override
	public void dataSaved(boolean saved) {
	}

	@Override
	public void clearData() {
	}
}
