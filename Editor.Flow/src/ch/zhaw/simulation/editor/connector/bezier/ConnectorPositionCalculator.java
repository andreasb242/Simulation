package ch.zhaw.simulation.editor.connector.bezier;

import java.awt.Point;

import ch.zhaw.simulation.editor.flow.connector.parameterarrow.BezierHelperPoint;
import ch.zhaw.simulation.model.element.SimulationObject;

public class ConnectorPositionCalculator {
	private SimulationObject object;
	private BezierHelperPoint connectorPoint;
	private Direction anchorDirection = Direction.RIGHT;

	public ConnectorPositionCalculator(SimulationObject object, BezierHelperPoint connectorPoint) {
		this.object = object;
		this.connectorPoint = connectorPoint;

		if (object == null) {
			throw new NullPointerException("object == null");
		}
		if (connectorPoint == null) {
			throw new NullPointerException("connectorPoint == null");
		}
	}

	public void dispose() {
		connectorPoint = null;
		object = null;

	}

	public Direction getAnchorDirection() {
		return anchorDirection;
	}
	
	private double getDistance(Point p1, Point p2) {
		double w = getDistance(p1.getX(), p2.getX());
		double h = getDistance(p1.getY(), p2.getY());
		return Math.sqrt(w * w + h * h);
	}

	private double getDistance(double i1, double i2) {
		if (i1 < i2) {
			return i2 - i1;
		}
		return i1 - i2;
	}

	public Point getPoint() {
		Point movePointPosition = connectorPoint.getPoint();

		Point top = new Point(object.getX() + object.getWidth() / 2, object.getY());
		Point bottom = new Point(object.getX() + object.getWidth() / 2, object.getY() + object.getHeight());

		Point left = new Point(object.getX(), object.getY() + object.getHeight() / 2);
		Point right = new Point(object.getX() + object.getWidth(), object.getY() + object.getHeight() / 2);

		double pTop = getDistance(movePointPosition, top);
		double pBottom = getDistance(movePointPosition, bottom);
		double pLeft = getDistance(movePointPosition, left);
		double pRight = getDistance(movePointPosition, right);

		// Unten (nidrigste Prioriätä)
		if (pBottom < pTop && pBottom < pRight && pBottom < pLeft) {
			anchorDirection = Direction.BOTTOM;
			return bottom;
		}

		if (pTop < pLeft && pTop < pRight) {
			anchorDirection = Direction.TOP;
			return top;
		}

		if (pLeft < pRight) {
			anchorDirection = Direction.LEFT;
			return left;
		}

		// default, höchste Priorität
		anchorDirection = Direction.RIGHT;
		return right;
	}
}