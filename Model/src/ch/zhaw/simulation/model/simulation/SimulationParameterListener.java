package ch.zhaw.simulation.model.simulation;

public interface SimulationParameterListener {

	public void pluginChanged(String plugin);

	public void propertyChanged(String property, Object newValue);

}
