package ch.zhaw.simulation.model.simulation;

public interface SimulationParameterListener {

	public void pluginChanged(String plugin);

	public void propertyChanged(String property, String newValue);
	public void propertyChanged(String property, double newValue);

}
