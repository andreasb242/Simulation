package ch.zhaw.simulation.plugin;

import ch.zhaw.simulation.model.SimulationType;

import javax.swing.JFrame;

public interface PluginDataProvider {
	public JFrame getParent();
	public ExecutionListener getExecutionListener();
	public SimulationType getSimulationType();
}
