package ch.zhaw.simulation.model.simulation;

/**
 * SimulationPlugin Listener
 * 
 * @author Andreas Butti
 */
public interface PluginChangeListener {

	/**
	 * Another simulation plugin was selected
	 * 
	 * @param plugin
	 *            The name of the Plugin (defined within the XML file)
	 */
	public void pluginChanged(String plugin);
}
