package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.element.SimulationData;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;

/**
 * This adapter prevents to implement all, even the unused, methods, so only the
 * needed methods need to be implemented
 * 
 * @author Andreas Butti
 */
public abstract class FlowSimulationAdapter implements FlowSimulationListener {

	@Override
	public void connectorAdded(AbstractConnectorData<?> c) {
	}

	@Override
	public void connectorRemoved(AbstractConnectorData<?> c) {
	}

	@Override
	public void connectorChanged(AbstractConnectorData<?> c) {
	}

	@Override
	public void dataAdded(SimulationData o) {
	}

	@Override
	public void dataChanged(SimulationData o) {
	}

	@Override
	public void dataRemoved(SimulationData o) {
	}

	@Override
	public void dataSaved(boolean saved) {
	}

	@Override
	public void clearData() {
	}
}
