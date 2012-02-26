package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.flow.connection.Connector;

public interface FlowSimulationListener extends SimulationListener {

	/**
	 * A connector was added
	 * 
	 * @param c
	 *            The connector
	 */
	public void connectorAdded(Connector<?> c);

	/**
	 * A connector was removed
	 * 
	 * @param c
	 *            The connector
	 */
	public void connectorRemoved(Connector<?> c);

	/**
	 * A connector was changed
	 * 
	 * @param c
	 *            The connector
	 */
	public void connectorChanged(Connector<?> c);

}
