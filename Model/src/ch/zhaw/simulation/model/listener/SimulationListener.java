package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.element.AbstractSimulationData;

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
	public void dataAdded(AbstractSimulationData o);

	/**
	 * An object was removed
	 * 
	 * @param o
	 *            The removed object
	 */
	public void dataRemoved(AbstractSimulationData o);

	/**
	 * A SimulationObject was changed (moved ... etc)
	 * 
	 * @param o
	 *            The changed object
	 */
	public void dataChanged(AbstractSimulationData o);

	/**
	 * The model was saved or changed
	 * 
	 * @param saved
	 *            true: the model was saved, false: the model was changed and
	 *            not saved
	 */
	public void dataSaved(boolean saved);

	/**
	 * The complete Model was cleared, this means everything has to be removed
	 */
	public void clearData();
}
