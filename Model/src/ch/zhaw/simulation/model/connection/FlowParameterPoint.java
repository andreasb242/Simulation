package ch.zhaw.simulation.model.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.NamedSimulationObject;


public class FlowParameterPoint extends NamedSimulationObject {

	private Object nextFlowValue;

	public FlowParameterPoint(int x, int y) {
		super(x, y);
	}

	public FlowParameterPoint(Point point) {
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

	public void setNextFlowValue(Object nextFlowValue) {
		this.nextFlowValue = nextFlowValue;
	}

	public Object getNextFlowValue() {
		return nextFlowValue;
	}

	@Override
	public String getDefaultName() {
		return "var";
	}
}
