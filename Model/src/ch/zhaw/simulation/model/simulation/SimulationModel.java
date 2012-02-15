package ch.zhaw.simulation.model.simulation;

import java.util.Vector;

import butti.javalibs.util.StringUtil;

public class SimulationModel {
	/**
	 * The Simulationplugin which is used
	 */
	private String plugin = null;

	private Vector<SimulationParameterListener> listener = new Vector<SimulationParameterListener>();

	public SimulationModel() {
	}

	public void setPlugin(String plugin) {
		if (StringUtil.equals(this.plugin, plugin)) {
			return;
		}
		this.plugin = plugin;
		fireTypeChanged();
	}

	public String getPlugin() {
		return plugin;
	}

	public void addSimulationParameterListener(SimulationParameterListener l) {
		listener.add(l);
	}

	public void removeSimulationParameterListener(SimulationParameterListener l) {
		listener.remove(l);
	}

	private void firePropertyChanged(String property, Object newValue) {
		for (SimulationParameterListener l : listener) {
			l.propertyChanged(property, newValue);
		}
	}

	private void fireTypeChanged() {
		for (SimulationParameterListener l : listener) {
			l.pluginChanged(plugin);
		}
	}
}
