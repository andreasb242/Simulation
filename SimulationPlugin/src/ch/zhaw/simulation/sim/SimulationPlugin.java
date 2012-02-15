package ch.zhaw.simulation.sim;

import javax.swing.JPanel;

import ch.zhaw.simulation.model.SimulationDocument;

import butti.javalibs.config.Settings;
import butti.plugin.definition.AbstractPlugin;

public interface SimulationPlugin extends AbstractPlugin {

	/**
	 * Gibt die Einstellungen des Simulationsplugin oder <code>null</code>
	 * zurück wenn keine vorhanden
	 */
	public JPanel getSettingsPanel();

	/**
	 * Initialisiert das Plugin mit den Einstellungen
	 */
	public void init(Settings settings);

	/**
	 * Prüft ob ein Model für die Simulation geeignet ist oder nicht
	 * 
	 * Aufruf (1) bei Simulation
	 * 
	 * @param model
	 *            Das zu simulierende Model
	 * @return <code>null</code> Wenn alles OK ist, oder sonst eine Meldung an
	 *         den Benutzer was nicht funktioniert
	 */
	public String checkModel(SimulationDocument model);

	/**
	 * Hier wird die Simulation vorbereitet, z.B. ein Simulationfile erzeugt
	 * 
	 * Aufruf (2) bei Simulation
	 * 
	 * @param model
	 *            Das Model das simuliert werden soll
	 * @throws Exception
	 *             Falls etwas schief geht, die Meldung wird dem Benutzer
	 *             angezeigt
	 */
	public void prepareSimulation(SimulationDocument model) throws Exception;

}
