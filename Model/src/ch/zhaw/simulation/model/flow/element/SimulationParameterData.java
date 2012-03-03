package ch.zhaw.simulation.model.flow.element;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

/**
 * An simulation parameter
 * 
 * @author Andreas Butti
 */
public class SimulationParameterData extends AbstractNamedSimulationData {

	public SimulationParameterData(int x, int y) {
		super(x, y);
	}

	@Override
	public int getHeight() {
		return 50;
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public String getDefaultName() {
		return "var";
	}
}
