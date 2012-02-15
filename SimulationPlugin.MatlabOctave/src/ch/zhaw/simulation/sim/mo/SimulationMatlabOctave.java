package ch.zhaw.simulation.sim.mo;

import javax.swing.JPanel;

import butti.javalibs.config.Settings;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.sim.SimulationPlugin;
import ch.zhaw.simulation.sim.mo.gui.SettingsGui;

public class SimulationMatlabOctave implements SimulationPlugin {
	private Settings settings;
	
	public SimulationMatlabOctave() {
	}

	@Override
	public void init(Settings settings) {
		this.settings = settings;
	}
	@Override
	public boolean load() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unload() {
	}

	@Override
	public JPanel getSettingsPanel() {
		return new SettingsGui(this.settings);
	}

	@Override
	public String checkModel(SimulationDocument model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepareSimulation(SimulationDocument model) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
