package ch.zhaw.simulation.model.flow;

public class SimulationGlobal extends NamedSimulationObject {

	public SimulationGlobal(int x, int y) {
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
		return "g";
	}
}
