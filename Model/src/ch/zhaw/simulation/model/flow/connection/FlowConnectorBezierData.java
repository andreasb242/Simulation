package ch.zhaw.simulation.model.flow.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.flow.BezierConnectorData;

public abstract class FlowConnectorBezierData implements BezierConnectorData {

	private Point helperPoint;

	@Override
	public Point getHelperPoint() {
		return helperPoint;
	}

	@Override
	public void setHelperPoint(Point point) {
		helperPoint = point;
	}
}
