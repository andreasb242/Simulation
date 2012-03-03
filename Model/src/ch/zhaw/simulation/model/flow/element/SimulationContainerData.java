package ch.zhaw.simulation.model.flow.element;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

/**
 * An simulation container
 * 
 * @author Andreas Butti
 */
public class SimulationContainerData extends AbstractNamedSimulationData {
	public SimulationContainerData(int x, int y) {
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
