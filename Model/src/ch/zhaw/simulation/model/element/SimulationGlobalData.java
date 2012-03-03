package ch.zhaw.simulation.model.element;


/**
 * A global simulation parameter, this don't need to be connected (and event
 * can't) to other object, but can be used without this connection
 * 
 * @author Andreas Butti
 */
public class SimulationGlobalData extends AbstractNamedSimulationData {

	public SimulationGlobalData(int x, int y) {
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
