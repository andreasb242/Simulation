package ch.zhaw.simulation.model.flow;


public class InfiniteData extends SimulationObject {

	public InfiniteData(int x, int y) {
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
	public String toString() {
		return "InfiniteData: " + hashCode();
	}
}
