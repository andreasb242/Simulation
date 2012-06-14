package ch.zhaw.simulation.plugin;

import java.util.Vector;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.config.Settings;
import butti.plugin.definition.AbstractPlugin;
import ch.zhaw.simulation.dialog.settings.SettingsPanel;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;

public interface SimulationPlugin extends AbstractPlugin {

	/**
	 * Returns a panel for the settings, called after <code>init</code>
	 * 
	 * This should return the panel even it the plugin is unloaded or not loaded
	 * yet
	 * 
	 */
	public SettingsPanel getSettingsPanel();

	/**
	 * Gets the settings panel for this simulation, or <code>null</code> if none
	 * 
	 * This is only called if the plugin is loaded
	 */
	public JXTaskPane getConfigurationSidebar(SimulationType type);

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
	public void checkDocument(SimulationDocument doc) throws SimulationModelException;

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
	public void executeSimulation(SimulationDocument doc) throws Exception;

	/**
	 * @return The result for <code>doc</code> or <code>null</code> if nothing
	 *         available
	 */
	public SimulationCollection getSimulationResults(SimulationDocument doc);

	/**
	 * @return The result for <code>doc</code> or <code>null</code> if nothing
	 *         available
	 */
	public Vector<XYDensityRaw> getXYResults(SimulationDocument doc);

	/**
	 * Cancel the current simulation, if possible
	 */
	public void cancelSimulation();

}
