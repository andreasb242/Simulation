package ch.zhaw.simulation.gui.control;

import ch.zhaw.simulation.model.flow.simulation.PluginChangeListener;
import ch.zhaw.simulation.model.flow.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.flow.simulation.SimulationParameterListener;
import ch.zhaw.simulation.sim.StandardParameter;
import butti.javalibs.config.Settings;

public class SimulationSettingsSaver implements SimulationParameterListener, PluginChangeListener {

	private Settings settings;
	private SimulationConfiguration model;

	public SimulationSettingsSaver(SimulationConfiguration model, Settings settings) {
		this.settings = settings;
		this.model = model;

		model.addSimulationParameterListener(this);
		model.addPluginChangeListener(this);
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
		settings.setSetting(StandardParameter.SIM_PROPERTY_STRING_PREFIX + property, newValue);
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		settings.setSetting(StandardParameter.SIM_PROPERTY_DOUBLE_PREFIX + property, newValue);
	}
}
