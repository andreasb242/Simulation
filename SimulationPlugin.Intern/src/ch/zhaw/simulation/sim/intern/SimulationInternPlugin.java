package ch.zhaw.simulation.sim.intern;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;


import butti.javalibs.config.Settings;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.PluginDataProvider;
import ch.zhaw.simulation.plugin.SimulationPlugin;
import ch.zhaw.simulation.sim.intern.main.Simulation;
import ch.zhaw.simulation.sim.intern.sidebar.InternSimulationSidebar;

public class SimulationInternPlugin implements SimulationPlugin {
	private Settings settings;
	private PluginDataProvider provider;
	private InternSimulationSidebar sidebar;

	public SimulationInternPlugin() {
	}

	@Override
	public void init(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		this.settings = settings;
		this.provider = provider;
		this.sidebar = new InternSimulationSidebar(config);
		
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
		return null;
	}

	@Override
	public void checkModel(SimulationDocument doc)  {
		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("only flow model supported currently");
		}
	}

	@Override
	public void prepareSimulation(SimulationDocument doc) throws Exception {
		Simulation sim = new Simulation(provider, this.settings, doc);
		sim.checkData();
		sim.startSimulation();
	}
	
	@Override
	public JXTaskPane getConfigurationSettingsSidebar() {
		return this.sidebar;
	}
}
