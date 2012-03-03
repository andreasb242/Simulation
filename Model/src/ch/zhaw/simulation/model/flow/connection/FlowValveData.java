package ch.zhaw.simulation.model.flow.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

/**
 * A valve, associated with a FlowConnector
 * 
 * @author Andreas Butti
 */
public class FlowValveData extends AbstractNamedSimulationData {
	public FlowValveData(int x, int y) {
		super(x, y);
	}

	public FlowValveData(Point point) {
		super(point.x, point.y);
	}

	@Override
	public int getHeight() {
		return 50;
	}

	@Override
	public int getWidth() {
		return 50;
	}

	public Point getPoint() {
		return new Point(getX(), getY());
	}

	@Override
	public String getDefaultName() {
		return "var";
	}
}
