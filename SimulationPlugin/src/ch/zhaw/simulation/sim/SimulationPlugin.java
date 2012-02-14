package ch.zhaw.simulation.sim;

import javax.swing.JPanel;

import butti.javalibs.config.Settings;
import butti.plugin.definition.AbstractPlugin;

public interface SimulationPlugin extends AbstractPlugin {

	/**
	 * Gibt die Einstellungen des Simulationsplugin oder <code>null</code>
	 * zurück wenn keine vorhanden
	 */
	public JPanel getSettingsPanel();

	public void init(Settings settings);

}
