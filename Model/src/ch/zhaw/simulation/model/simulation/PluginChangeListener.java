package ch.zhaw.simulation.model.simulation;

/**
 * SimulationPlugin Listener
 * 
 * @author Andreas Butti
 */
public interface PluginChangeListener {

	/**
	 * Another simulation pluginName was selected
	 * 
	 * @param pluginName
	 *            The name of the Plugin (defined within the XML file)
	 */
	public void pluginChanged(String pluginName);
}
