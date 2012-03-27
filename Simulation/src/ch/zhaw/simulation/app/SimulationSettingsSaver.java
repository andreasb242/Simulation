package ch.zhaw.simulation.app;

import ch.zhaw.simulation.model.simulation.PluginChangeListener;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;
import ch.zhaw.simulation.plugin.StandardParameter;
import butti.javalibs.config.Settings;

public class SimulationSettingsSaver implements SimulationParameterListener, PluginChangeListener {
	private Settings settings;
	private SimulationConfiguration configuration;

	public SimulationSettingsSaver(SimulationConfiguration configuration, Settings settings) {
		this.settings = settings;
		this.configuration = configuration;

		configuration.addSimulationParameterListener(this);
		configuration.addPluginChangeListener(this);
	}

	public void load() {
		String pluginName = settings.getSetting("simulation.plugin");
		if (pluginName != null) {
			configuration.setSelectedPluginName(pluginName);
		}
	}

	@Override
	public void pluginChanged(String pluginName) {
		settings.setSetting("simulation.plugin", pluginName);
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
