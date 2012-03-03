package ch.zhaw.simulation.model.simulation;

/**
 * Listener for SimulationParameter
 * 
 * @author Andreas Butti
 */
public interface SimulationParameterListener {
	/**
	 * A Property of the simulation parameter has changed
	 * 
	 * @param property
	 *            The key
	 * @param newValue
	 *            Thew new value
	 */
	public void propertyChanged(String property, String newValue);

	/**
	 * A Property of the simulation parameter has changed
	 * 
	 * @param property
	 *            The key
	 * @param newValue
	 *            Thew new value
	 */
	public void propertyChanged(String property, double newValue);
}
