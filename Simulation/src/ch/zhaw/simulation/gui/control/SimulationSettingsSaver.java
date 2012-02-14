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

	// TODO: im Dokument speichern, nicht global
	
	public void load() {
		// TODO !!!!!!!
//		double dt = settings.getSetting("simulation.dt", 0.1);
//		model.setDt(dt);
//
//		double start = settings.getSetting("simulation.start", 0);
//		model.setStartTime(start);
//
//		double end = settings.getSetting("simulation.end", 100);
//		model.setEndtime(end);

		String plugin = settings.getSetting("simulation.plugin");

		model.setPlugin(plugin);
	}

	@Override
	public void pluginChanged(String plugin) {
//		settings.setSetting("simulation.plugin", plugin);
	}

	@Override
	public void propertyChanged(String property, Object newValue) {
//		settings.setSetting("simulation.plugin", plugin);
	}
}
