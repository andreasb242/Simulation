package ch.zhaw.simulation.model.flow.element;

import ch.zhaw.simulation.model.element.NamedSimulationObject;

/**
 * An simulation density container
 * 
 * @author Andreas Butti
 */
public class SimulationDensityContainer extends NamedSimulationObject {
	public SimulationDensityContainer(int x, int y) {
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
		return "d";
	}
}
