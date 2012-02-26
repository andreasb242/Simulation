package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.Connector;

/**
 * This adapter prevents to implement all, even the unused, methods, so only the
 * needed methods need to be implemented
 * 
 * @author Andreas Butti
 */
public abstract class FlowSimulationAdapter implements FlowSimulationListener {

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
