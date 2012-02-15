package ch.zhaw.simulation.gui.control;

import ch.zhaw.simulation.model.simulation.SimulationModel;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;
import butti.javalibs.config.Settings;

public class SimulationSettingsSaver implements SimulationParameterListener {

	private Settings settings;
	private SimulationModel model;

	public SimulationSettingsSaver(SimulationModel model, Settings settings) {
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
		System.out.println("plugin selected: " + plugin);
		
		settings.setSetting("simulation.plugin", plugin);
	}

	@Override
	public void propertyChanged(String property, Object newValue) {
//		settings.setSetting("simulation.plugin", plugin);
	}
}
