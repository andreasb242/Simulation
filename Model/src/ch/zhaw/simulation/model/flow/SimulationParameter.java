package ch.zhaw.simulation.model.flow;

/**
 * An simulation parameter
 * 
 * @author Andreas Butti
 */
public class SimulationParameter extends NamedSimulationObject {

	public SimulationParameter(int x, int y) {
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
