package ch.zhaw.simulation.model.flow;

import ch.zhaw.simulation.model.flow.connection.Connector;

public interface SimulationListener {
	public void dataAdded(SimulationObject o);

	public void dataRemoved(SimulationObject o);

	public void dataChanged(SimulationObject o);

	public void dataSaved(boolean saved);

	public void connectorAdded(Connector<?> c);

	public void connectorRemoved(Connector<?> c);

	public void connectorChanged(Connector<?> c);

	public void clearData();
}
