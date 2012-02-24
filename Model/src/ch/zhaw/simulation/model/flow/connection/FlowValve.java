package ch.zhaw.simulation.model.flow.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.flow.NamedSimulationObject;

/**
 * A valve, associated with a FlowConnector
 * 
 * @author Andreas Butti
 */
public class FlowValve extends NamedSimulationObject {
	public FlowValve(int x, int y) {
		super(x, y);
	}

	public FlowValve(Point point) {
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
