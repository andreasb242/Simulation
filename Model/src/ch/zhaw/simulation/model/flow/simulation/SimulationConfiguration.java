package ch.zhaw.simulation.model.flow.simulation;

import java.util.HashMap;
import java.util.Vector;

import butti.javalibs.util.StringUtil;

/**
 * The configuration of this simulation
 * 
 * @author Andreas Butti
 */
public class SimulationConfiguration {
	/**
	 * The Simulationplugin which is used
	 */
	private String plugin = null;

	// TODO !! docu
	
	private Vector<SimulationParameterListener> listener = new Vector<SimulationParameterListener>();
	private Vector<PluginChangeListener> pluginChangeListener = new Vector<PluginChangeListener>();

	private HashMap<String, String> stringParameter = new HashMap<String, String>();
	private HashMap<String, Double> doubleParameter = new HashMap<String, Double>();

	public SimulationConfiguration() {
	}

	public void setPlugin(String plugin) {
		if (StringUtil.equals(this.plugin, plugin)) {
			return;
		}
		this.plugin = plugin;
		fireTypeChanged();
	}

	/**
	 * The current selected simulation plugin
	 */
	public String getPlugin() {
		return plugin;
	}

	public HashMap<String, String> getStringParameter() {
		return stringParameter;
	}

	public HashMap<String, Double> getDoubleParameter() {
		return doubleParameter;
	}

	public void setParameter(String name, String value) {
		stringParameter.put(name, value);
		firePropertyChanged(name, value);
	}

	public void setParameter(String name, double value) {
		doubleParameter.put(name, value);
		firePropertyChanged(name, value);
	}

	public String getParameter(String name, String defaultValue) {
		String value = stringParameter.get(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public double getParameter(String name, double defaultValue) {
		Double value = doubleParameter.get(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public void addSimulationParameterListener(SimulationParameterListener l) {
		listener.add(l);
	}

	public void removeSimulationParameterListener(SimulationParameterListener l) {
		listener.remove(l);
	}

	public void addPluginChangeListener(PluginChangeListener l) {
		pluginChangeListener.add(l);
	}

	public void removePluginChangeListener(PluginChangeListener l) {
		pluginChangeListener.remove(l);
	}

	private void firePropertyChanged(String property, String newValue) {
		for (SimulationParameterListener l : listener) {
			l.propertyChanged(property, newValue);
		}
	}

	private void firePropertyChanged(String property, double newValue) {
		for (SimulationParameterListener l : listener) {
			l.propertyChanged(property, newValue);
		}
	}

	private void fireTypeChanged() {
		for (PluginChangeListener l : pluginChangeListener) {
			l.pluginChanged(plugin);
		}
	}

	public void clear() {
		stringParameter.clear();
		doubleParameter.clear();
	}
}
