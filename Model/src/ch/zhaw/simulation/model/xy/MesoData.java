package ch.zhaw.simulation.model.xy;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

public class MesoData extends AbstractNamedSimulationData {

	public MesoData(int x, int y) {
		super(x, y);
	}

	@Override
	public String getDefaultName() {
		return "a";
	}

	@Override
	public int getWidth() {
		return 50;
	}

	@Override
	public int getHeight() {
		return 50;
	}

}
