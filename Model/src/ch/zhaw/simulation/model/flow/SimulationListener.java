package ch.zhaw.simulation.model.flow;

import ch.zhaw.simulation.model.flow.connection.Connector;

/**
 * Listener for the simulation flow model
 * 
 * @author Andreas Butti
 */
public interface SimulationListener {

	/**
	 * An object was added
	 * 
	 * @param o
	 *            The added object
	 */
	public void dataAdded(SimulationObject o);

	/**
	 * An object was removed
	 * 
	 * @param o
	 *            The removed object
	 */
	public void dataRemoved(SimulationObject o);

	/**
	 * A SimulationObject was changed (moved ... etc)
	 * 
	 * @param o
	 *            The changed object
	 */
	public void dataChanged(SimulationObject o);

	/**
	 * The model was saved or changed
	 * 
	 * @param saved
	 *            true: the model was saved, false: the model was changed and
	 *            not saved
	 */
	public void dataSaved(boolean saved);

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

	/**
	 * The complete Model was cleared, this means everything has to be removed
	 */
	public void clearData();
}
