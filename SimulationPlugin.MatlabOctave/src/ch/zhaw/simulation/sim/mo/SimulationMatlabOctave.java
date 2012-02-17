package ch.zhaw.simulation.sim.mo;

import java.awt.Window;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.config.Settings;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.sim.SimulationPlugin;
import ch.zhaw.simulation.sim.mo.codegen.AbstractCodegen;
import ch.zhaw.simulation.sim.mo.codegen.RungeKuttaCodegen;
import ch.zhaw.simulation.sim.mo.gui.SettingsGui;
import ch.zhaw.simulation.sim.sidebar.DefaultSimulationSidebar;

public class SimulationMatlabOctave implements SimulationPlugin {
	private Settings settings;
	private Window parent;
	private DefaultSimulationSidebar sidebar = new DefaultSimulationSidebar();

	public SimulationMatlabOctave() {
	}

	@Override
	public void init(Settings settings, Window parent) {
		this.settings = settings;
		this.parent = parent;
	}

	@Override
	public boolean load() throws Exception {
		return true;
	}

	@Override
	public void unload() {
	}

	@Override
	public JPanel getSettingsPanel() {
		return new SettingsGui(this.settings, this.parent);
	}

	@Override
	public String checkModel(SimulationDocument model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepareSimulation(SimulationDocument model) throws Exception {
		AbstractCodegen codegen = new RungeKuttaCodegen();
		codegen.setWorkingFolder(settings.getSetting("workpath"));

		codegen.crateSimulation(model);
	}

	@Override
	public JXTaskPane getConfigurationSettingsSidebar() {
		return sidebar;
	}
}
