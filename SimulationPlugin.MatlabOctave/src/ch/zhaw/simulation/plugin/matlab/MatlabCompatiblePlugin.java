package ch.zhaw.simulation.plugin.matlab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JPanel;

import butti.javalibs.config.Settings;
import butti.javalibs.dirwatcher.DirectoryWatcher;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.ExecutionListener.FinishState;
import ch.zhaw.simulation.plugin.PluginDataProvider;
import ch.zhaw.simulation.plugin.SimulationPlugin;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator;
import ch.zhaw.simulation.plugin.matlab.gui.SettingsGui;
import ch.zhaw.simulation.plugin.matlab.optimizer.FlowModelOptimizer;
import ch.zhaw.simulation.plugin.matlab.optimizer.ModelOptimizer;
import ch.zhaw.simulation.plugin.matlab.optimizer.XYModelOptimizer;
import ch.zhaw.simulation.plugin.matlab.sidebar.MatlabConfigurationSidebar;
import ch.zhaw.simulation.plugin.matlab.util.OutputReaderThread;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;

public class MatlabCompatiblePlugin implements SimulationPlugin {
	private Settings settings;
	private MatlabConfigurationSidebar sidebar;
	private ModelOptimizer optimizer;
	private SimulationConfiguration config;

	protected PluginDataProvider provider;
	protected DirectoryWatcher watcher;
	protected MatlabFinishListener finishListener;

	private Process process;

	public MatlabCompatiblePlugin() {
	}

	@Override
	public DefaultConfigurationSidebar getConfigurationSidebar() {
		return sidebar;
	}

	@Override
	public void init(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		this.settings = settings;
		this.config = config;
		this.provider = provider;
		this.sidebar = new MatlabConfigurationSidebar(config, provider.getSimulationType());
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
		if (doc.getType() == SimulationType.FLOW_SIMULATION) {
			optimizer = new FlowModelOptimizer(doc.getFlowModel());
		} else if (doc.getType() == SimulationType.XY_MODEL) {
			optimizer = new XYModelOptimizer(doc.getXyModel());
		}
		optimizer.optimize();
	}

	@Override
	public void executeFlowSimulation(SimulationDocument doc) throws Exception {
		String workpath = settings.getSetting(MatlabParameter.WORKPATH, MatlabParameter.DEFAULT_WORKPATH);
		String filename = null;

		AbstractCodeGenerator codeGenerator = sidebar.getSelectedNumericMethod().getCodeGenerator();

		try {
			finishListener.updateWorkpath(workpath);
			watcher.start(workpath);
			provider.getExecutionListener().executionStarted("Simulation läuft...");

			codeGenerator.setWorkingFolder(workpath);
			codeGenerator.generateSimulation(doc);
			filename = codeGenerator.getGeneratedFile();
			startApplication(workpath, filename);
		} catch (IOException e) {
			watcher.stop();
			provider.getExecutionListener().executionFinished(e.getMessage(), FinishState.ERROR);
			throw e;
		} catch (IllegalArgumentException e) {
			watcher.stop();
			provider.getExecutionListener().executionFinished(e.getMessage(), FinishState.ERROR);
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
			arguments = "-nosplash -nodesktop -minimize -wait -sd " + dir + " -r " + filename;
		} else if (t == MatlabTool.OCTAVE) {
			executable = settings.getSetting(MatlabParameter.EXEC_OCTAVE_PATH, MatlabParameter.DEFAULT_EXEC_OCTAVE_PATH);
			arguments = "--no-line-editing --exec-path " + dir + " " + filename + ".m";
		} else if (t == MatlabTool.SCILAB) {
			executable = settings.getSetting(MatlabParameter.EXEC_SCILAB_PATH, MatlabParameter.DEFAULT_EXEC_SCILAB_PATH);
			arguments = "";
		} else {
			throw new IllegalArgumentException();
		}

		System.out.println("MatlabCompatiblePlugin: " + executable + " " + arguments);
		this.process = Runtime.getRuntime().exec(executable + " " + arguments);

		ActionListener errorListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				watcher.stop();
				provider.getExecutionListener().executionFinished(e.getActionCommand(), FinishState.ERROR);
			}
		};

		OutputReaderThread stdout = new OutputReaderThread("[" + t + "] ", process.getInputStream(), System.out);
		OutputReaderThread stderr = new OutputReaderThread("[" + t + "] ", process.getInputStream(), System.err);

		stdout.addListener(errorListener);
		stderr.addListener(errorListener);

		stdout.start();
		stderr.start();
	}

	@Override
	public void cancelSimulation() {
		provider.getExecutionListener().setExecutionMessage("Wird abgebrochen...");
		watcher.stop();

		this.process.destroy();

		try {
			int retCode = this.process.waitFor();
			System.out.println("Process exited with: " + retCode);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			provider.getExecutionListener().executionFinished(null, FinishState.CANCELED);
		}
	}
}