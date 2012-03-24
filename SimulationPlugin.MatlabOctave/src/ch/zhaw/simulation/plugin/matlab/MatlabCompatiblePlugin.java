package ch.zhaw.simulation.plugin.matlab;

import javax.swing.*;

import butti.javalibs.dirwatcher.DirectoryWatcher;
import butti.javalibs.dirwatcher.FileListener;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.gui.BusyDialog;
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

import java.io.File;

public class MatlabCompatiblePlugin implements SimulationPlugin {
	private Settings settings;
	private PluginDataProvider provider;
	private MatlabConfigurationSidebar sidebar;
	private ModelOptimizer optimizer;
	private DirectoryWatcher watcher;
	private MatlabFinishListener finishListener;
	private BusyDialog busyDialog;

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
		this.watcher = new DirectoryWatcher(settings.getSetting("workpath"), 1000);
		this.busyDialog = new BusyDialog(provider.getParent());
		this.finishListener = new MatlabFinishListener(provider, watcher, busyDialog);
		this.watcher.addResourceListener(this.finishListener);
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
		String workpath = settings.getSetting("workpath");

		AbstractCodeGenerator codeGenerator = sidebar.getSelectedNumericMethod().getCodeGenerator();
		finishListener.updateWorkpath(workpath);
		watcher.start();
		busyDialog.setVisible(true);

		codeGenerator.setWorkingFolder(workpath);
		codeGenerator.executeSimulation(doc);

	}

	@Override
	public SimulationCollection getSimulationResults() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}