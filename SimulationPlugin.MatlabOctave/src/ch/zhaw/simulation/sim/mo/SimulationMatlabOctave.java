package ch.zhaw.simulation.sim.mo;

import java.awt.Window;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.sim.SimulationPlugin;
import ch.zhaw.simulation.sim.mo.codegen.AbstractCodegen;
import ch.zhaw.simulation.sim.mo.codegen.EulerCodegen;
import ch.zhaw.simulation.sim.mo.gui.SettingsGui;
import ch.zhaw.simulation.sim.sidebar.DefaultSimulationSidebar;

public class SimulationMatlabOctave implements SimulationPlugin {
	private Settings settings;
	private Window parent;
	private DefaultSimulationSidebar sidebar;
	private ModelOptimizer optimizer;

	public SimulationMatlabOctave() {
	}

	@Override
	public void init(Settings settings, SimulationConfiguration config, Window parent) {
		this.settings = settings;
		this.parent = parent;
		this.sidebar = new DefaultSimulationSidebar(config);
	}

	@Override
	public boolean load() throws Exception {
		this.sidebar.load();
		return true;
	}

	@Override
	public void unload() {
		this.sidebar.unload();
	}

	@Override
	public JPanel getSettingsPanel() {
		return new SettingsGui(this.settings, this.parent);
	}

	@Override
	public void checkModel(SimulationDocument model) throws SimulationModelException {
		optimizer = new ModelOptimizer(model);

		optimizer.optimize();
	}

	@Override
	public void prepareSimulation(SimulationDocument model) throws Exception {
		AbstractCodegen codegen = new EulerCodegen();
		codegen.setWorkingFolder(settings.getSetting("workpath"));

		codegen.crateSimulation(model);
	}

	@Override
	public JXTaskPane getConfigurationSettingsSidebar() {
		return sidebar;
	}
}
