package ch.zhaw.simulation.sim.mo;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.flow.simulation.SimulationConfiguration;
import ch.zhaw.simulation.sim.PluginDataProvider;
import ch.zhaw.simulation.sim.SimulationPlugin;
import ch.zhaw.simulation.sim.mo.codegen.AbstractCodegen;
import ch.zhaw.simulation.sim.mo.codegen.EulerCodegen;
import ch.zhaw.simulation.sim.mo.gui.SettingsGui;
import ch.zhaw.simulation.sim.sidebar.DefaultSimulationSidebar;

public class SimulationMatlabOctave implements SimulationPlugin {
	private Settings settings;
	private PluginDataProvider provider;
	private DefaultSimulationSidebar sidebar;
	private ModelOptimizer optimizer;

	public SimulationMatlabOctave() {
	}

	@Override
	public void init(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		this.settings = settings;
		this.provider = provider;
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
		return new SettingsGui(this.settings, provider.getParent());
	}

	@Override
	public void checkModel(SimulationDocument doc) throws SimulationModelException {
		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("only flow model supported currently");
		}

		optimizer = new ModelOptimizer(doc.getFlowModel());

		optimizer.optimize();
	}

	@Override
	public void prepareSimulation(SimulationDocument doc) throws Exception {
		AbstractCodegen codegen = new EulerCodegen();
		codegen.setWorkingFolder(settings.getSetting("workpath"));

		codegen.crateSimulation(doc);
	}

	@Override
	public JXTaskPane getConfigurationSettingsSidebar() {
		return sidebar;
	}
}
