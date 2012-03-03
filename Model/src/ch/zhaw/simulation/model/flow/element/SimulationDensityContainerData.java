package ch.zhaw.simulation.model.flow.element;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

/**
 * An simulation density container
 * 
 * @author Andreas Butti
 */
public class SimulationDensityContainerData extends AbstractNamedSimulationData {
	public SimulationDensityContainerData(int x, int y) {
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
