package ch.zhaw.simulation.model.simulation;

public interface SimulationParameterListener {
	public void propertyChanged(String property, String newValue);
	public void propertyChanged(String property, double newValue);
}
