package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;

public interface FlowSimulationListener extends SimulationListener {

	/**
	 * A connector was added
	 * 
	 * @param c
	 *            The connector
	 */
	public void connectorAdded(AbstractConnectorData<?> c);

	/**
	 * A connector was removed
	 * 
	 * @param c
	 *            The connector
	 */
	public void connectorRemoved(AbstractConnectorData<?> c);

	/**
	 * A connector was changed
	 * 
	 * @param c
	 *            The connector
	 */
	public void connectorChanged(AbstractConnectorData<?> c);

}
