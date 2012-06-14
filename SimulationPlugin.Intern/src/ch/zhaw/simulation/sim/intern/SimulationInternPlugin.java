package ch.zhaw.simulation.sim.intern;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.config.Settings;
import ch.zhaw.simulation.dialog.settings.SettingsPanel;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.ExecutionListener;
import ch.zhaw.simulation.plugin.ExecutionListener.FinishState;
import ch.zhaw.simulation.plugin.PluginDataProvider;
import ch.zhaw.simulation.plugin.SimulationPlugin;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.sidebar.NotSupportedSidebar;
import ch.zhaw.simulation.sim.intern.main.Simulation;
import ch.zhaw.simulation.sim.intern.sidebar.InternSimulationSidebar;

public class SimulationInternPlugin implements SimulationPlugin {
	private PluginDataProvider provider;
	private InternSimulationSidebar sidebar;
	private SimulationCollection collection;
	private Simulation sim;

	public SimulationInternPlugin() {
	}

	@Override
	public void init(Settings settings, SimulationConfiguration config, PluginDataProvider provider) {
		this.provider = provider;
		this.sidebar = new InternSimulationSidebar(config, provider.getSimulationType());
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
	public SettingsPanel getSettingsPanel() {
		return null;
	}

	@Override
	public JXTaskPane getConfigurationSidebar(SimulationType type) {
		if (type == SimulationType.FLOW_SIMULATION) {
			return this.sidebar;
		}

		return new NotSupportedSidebar(type, "Intern Simulation");
	}

	@Override
	public void checkDocument(SimulationDocument doc) throws SimulationModelException {
		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("Intern Simulation supports currently only flow model");
		}
	}

	@Override
	public void executeSimulation(SimulationDocument doc) throws Exception {
		final ExecutionListener executionListener = provider.getExecutionListener();

		this.sim = new Simulation(doc);
		sim.checkData();

		try {
			sim.initSimulation();
		} catch (Exception e) {
			executionListener.executionFinished(e.getMessage(), FinishState.ERROR);
			e.printStackTrace();
			return;
		}

		sim.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					executionListener.setState((Integer) evt.getNewValue());
				} else if ("state".equals(evt.getPropertyName())) {
					if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
						try {
							SimulationInternPlugin.this.collection = sim.getSimulationResult();
							executionListener.executionFinished(null, FinishState.SUCCESSFULLY);
						} catch (java.util.concurrent.CancellationException e) {
							executionListener.executionFinished(null, FinishState.CANCELED);
						} catch (Exception e) {
							executionListener.executionFinished(e.getMessage(), FinishState.ERROR);
							e.printStackTrace();
						}
					}
				}
			}
		});

		executionListener.executionStarted("Simulieren...");
		sim.execute();
	}

	@Override
	public SimulationCollection getSimulationResults(SimulationDocument doc) {
		return this.collection;
	}

	@Override
	public Vector<XYDensityRaw> getXYResults(SimulationDocument doc) {
		// not used
		return null;
	}

	@Override
	public void cancelSimulation() {
		this.sim.cancelSimulation();
	}
}
