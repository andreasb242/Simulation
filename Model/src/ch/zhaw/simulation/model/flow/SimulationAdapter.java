package ch.zhaw.simulation.model.flow;

import ch.zhaw.simulation.model.flow.connection.Connector;

public abstract class SimulationAdapter implements SimulationListener {

	@Override
	public void connectorAdded(Connector<?> c) {
	}

	@Override
	public void connectorRemoved(Connector<?> c) {
	}

	@Override
	public void connectorChanged(Connector<?> c) {
	}

	@Override
	public void dataAdded(SimulationObject o) {
	}

	@Override
	public void dataChanged(SimulationObject o) {
	}

	@Override
	public void dataRemoved(SimulationObject o) {
	}

	@Override
	public void dataSaved(boolean saved) {
	}

	@Override
	public void clearData() {
	}
}
