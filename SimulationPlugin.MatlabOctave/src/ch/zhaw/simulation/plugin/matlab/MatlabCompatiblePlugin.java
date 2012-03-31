package ch.zhaw.simulation.plugin.matlab;

import javax.swing.*;

import butti.javalibs.dirwatcher.DirectoryWatcher;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
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

import java.io.IOException;

public class MatlabCompatiblePlugin implements SimulationPlugin {
	private Settings settings;
	private MatlabConfigurationSidebar sidebar;
	private ModelOptimizer optimizer;
	private SimulationConfiguration config;

	protected PluginDataProvider provider;
	protected DirectoryWatcher watcher;
	protected MatlabFinishListener finishListener;

	public MatlabCompatiblePlugin() {
	}

	@Override
	public JXTaskPane getConfigurationSidebar() {
		return sidebar;
	}

	@Override
	public void init(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		this.settings = settings;
		this.config = config;
		this.provider = provider;
		this.sidebar = new MatlabConfigurationSidebar(config);
		this.watcher = new DirectoryWatcher(1000);
		this.finishListener = new MatlabFinishListener(this);
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
		String workpath = settings.getSetting(MatlabParameter.WORKPATH, MatlabParameter.DEFAULT_WORKPATH);

		AbstractCodeGenerator codeGenerator = sidebar.getSelectedNumericMethod().getCodeGenerator();
		
		try {
			finishListener.updateWorkpath(workpath);
			watcher.start(workpath);
			provider.getExecutionListener().executionStarted("Simulation läuft..");
	
			codeGenerator.setWorkingFolder(workpath);
			codeGenerator.executeSimulation(doc);
			startApplication(workpath, codeGenerator.getGeneratedFile());
		} catch (IOException e) {
			watcher.stop();
			provider.getExecutionListener().executionFinished();
			throw e;
		} catch (IllegalArgumentException e) {
			watcher.stop();
			provider.getExecutionListener().executionFinished();
			throw e;
		}

	}

	@Override
	public SimulationCollection getSimulationResults(SimulationDocument doc) {
		return new SimulationResultParser(doc, config).parse(settings.getSetting(MatlabParameter.WORKPATH, MatlabParameter.DEFAULT_WORKPATH));
	}

	protected void startApplication(String dir, String filename) throws IllegalArgumentException, IOException {
		String tool = settings.getSetting(MatlabParameter.TOOL, MatlabParameter.DEFAULT_TOOL);
		MatlabTool t = MatlabTool.fromString(tool);
		String executable = null;
		String arguments = null;

		if (t == MatlabTool.MATLAB) {
			executable = settings.getSetting(MatlabParameter.EXEC_MATLAB_PATH, MatlabParameter.DEFAULT_EXEC_MATLAB_PATH);
			arguments = "-nosplash -nodesktop -minimize -sd " + dir + " -r " + filename;
		} else if (t == MatlabTool.OCTAVE) {
			executable = settings.getSetting(MatlabParameter.EXEC_OCTAVE_PATH, MatlabParameter.DEFAULT_EXEC_OCTAVE_PATH);
			arguments = "";
		} else if (t == MatlabTool.SCILAB) {
			executable = settings.getSetting(MatlabParameter.EXEC_SCILAB_PATH, MatlabParameter.DEFAULT_EXEC_SCILAB_PATH);
			arguments = "";
		} else {
			throw new IllegalArgumentException();
		}

		Runtime.getRuntime().exec(executable + " " + arguments);
	}
}