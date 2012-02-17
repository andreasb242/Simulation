package ch.zhaw.simulation.gui.control;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;
import butti.javalibs.config.Settings;

public class SimulationSettingsSaver implements SimulationParameterListener {

	private Settings settings;
	private SimulationConfiguration model;

	public SimulationSettingsSaver(SimulationConfiguration model, Settings settings) {
		this.settings = settings;
		this.model = model;

		model.addSimulationParameterListener(this);
	}

	public void load() {
		String plugin = settings.getSetting("simulation.plugin");
		model.setPlugin(plugin);
	}

	@Override
	public void pluginChanged(String plugin) {
		settings.setSetting("simulation.plugin", plugin);
	}

	@Override
	public void propertyChanged(String property, String newValue) {
		settings.setSetting("simulation.sproperty." + property, newValue);
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		settings.setSetting("simulation.dproperty." + property, newValue);
	}
}
