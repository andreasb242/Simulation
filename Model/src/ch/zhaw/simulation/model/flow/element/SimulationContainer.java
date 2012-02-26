package ch.zhaw.simulation.model.flow.element;

import ch.zhaw.simulation.model.element.NamedSimulationObject;

/**
 * An simulation container
 * 
 * @author Andreas Butti
 */
public class SimulationContainer extends NamedSimulationObject {
	public SimulationContainer(int x, int y) {
		super(x, y);
	}

	@Override
	public int getHeight() {
		return 70;
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public String getDefaultName() {
		return "con";
	}
}
