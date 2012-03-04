package ch.zhaw.simulation.plugin.matlab;

import javax.swing.JPanel;

import ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.gui.SettingsGui;
import ch.zhaw.simulation.plugin.matlab.sidebar.MatlabConfigurationSidebar;
import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.PluginDataProvider;
import ch.zhaw.simulation.plugin.SimulationPlugin;

public class MatlabCompatiblePlugin implements SimulationPlugin {
	private Settings settings;
	private PluginDataProvider provider;
	private MatlabConfigurationSidebar sidebar;
	private ModelOptimizer optimizer;

	public MatlabCompatiblePlugin() {
	}

	@Override
	public JXTaskPane getConfigurationSidebar() {
		return sidebar;
	}

	@Override
	public void init(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		this.settings = settings;
		this.provider = provider;
		this.sidebar = new MatlabConfigurationSidebar(config);
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
	public void checkDocument(SimulationDocument doc) throws SimulationModelException {
		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("only flow model supported currently");
		}

		optimizer = new ModelOptimizer(doc.getFlowModel());

		optimizer.optimize();
	}

	@Override
	public void executeSimulation(SimulationDocument doc) throws Exception {
		AbstractCodeGenerator codeGenerator = sidebar.getSelectedNumericMethod().getCodeGenerator();

		codeGenerator.setWorkingFolder(settings.getSetting("workpath"));
		codeGenerator.executeSimulation(doc);

	}
}