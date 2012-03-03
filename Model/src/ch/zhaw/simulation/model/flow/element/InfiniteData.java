package ch.zhaw.simulation.model.flow.element;

import ch.zhaw.simulation.model.element.SimulationData;

/**
 * This item is used if there is a flow from "noting" into a container, or from
 * a container to "nothing"
 * 
 * @author Andreas Butti
 */
public class InfiniteData extends SimulationData {

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
