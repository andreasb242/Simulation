package ch.zhaw.simulation.plugin;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.config.Settings;
import butti.plugin.definition.AbstractPlugin;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;

public interface SimulationPlugin extends AbstractPlugin {

	/**
	 * Returns a panel for the settings, called after <code>init</code>
	 * 
	 * This should return the panel even it the plugin is unloaded or not loaded
	 * yet
	 * 
	 */
	public JPanel getSettingsPanel();

	/**
	 * Gets the settings panel for this simulation, or <code>null</code> if none
	 * 
	 * This is only called if the plugin is loaded
	 */
	public JXTaskPane getConfigurationSettingsSidebar();

	/**
	 * Initialisiert das Plugin mit den Einstellungen
	 */
	public void init(Settings settings, SimulationConfiguration config, PluginDataProvider provider);

	/**
	 * Prüft ob ein Model für die Simulation geeignet ist oder nicht
	 * 
	 * Aufruf (1) bei Simulation
	 * 
	 * @param doc
	 *            Das zu simulierende Model
	 */
	public void checkModel(SimulationDocument doc) throws SimulationModelException;

	/**
	 * Hier wird die Simulation vorbereitet, z.B. ein Simulationfile erzeugt
	 * 
	 * Aufruf (2) bei Simulation
	 * 
	 * @param doc
	 *            Das Model das simuliert werden soll
	 * @throws Exception
	 *             Falls etwas schief geht, die Meldung wird dem Benutzer
	 *             angezeigt
	 */
	public void prepareSimulation(SimulationDocument doc) throws Exception;

}
