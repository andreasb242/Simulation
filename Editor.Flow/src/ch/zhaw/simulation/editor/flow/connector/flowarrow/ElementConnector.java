package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Point;

import ch.zhaw.simulation.model.flow.SimulationObject;


public class ElementConnector {
	private SimulationObject object;
	private SimulationObject otherObject;
	private Direction direction;

	public ElementConnector(SimulationObject object, SimulationObject otherObject) {
		this.object = object;
		this.otherObject = otherObject;
		
		if(object == null) {
			throw new NullPointerException("object == null");
		}
		if(otherObject == null) {
			throw new NullPointerException("otherObject == null");
		}
	}

	public void dispose() {
		object = null;
		otherObject = null;
		direction = null;
	}

	private double getDistance(SimulationObject object, Point p2) {
		double w = getDistance(object.getX(), p2.getX());
		double h = getDistance(object.getY(), p2.getY());
		return Math.sqrt(w * w + h * h);
	}
	
	private double getDistance(double i1, double i2) {
		if(i1 < i2) {
			return i2 - i1;
		}
		return i1 - i2;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public Point getPoint() {
		Point top = new Point(object.getX() + object.getWidth() / 2, object.getY());
		Point bottom = new Point(object.getX() + object.getWidth() / 2, object.getY() + object.getHeight());

		Point left = new Point(object.getX(), object.getY() + object.getHeight() / 2);
		Point right = new Point(object.getX() + object.getWidth(), object.getY() + object.getHeight() / 2);

		double pTop = getDistance(otherObject, top);
		double pBottom = getDistance(otherObject, bottom);
		double pLeft = getDistance(otherObject, left);
		double pRight = getDistance(otherObject, right);
		
		// Unten (nidrigste Prioriätä)
		if(pBottom < pTop && pBottom < pRight && pBottom < pLeft) {
			direction = Direction.BOTTOM;
			return bottom;
		}
		
		// Rechts
		if(pTop < pLeft && pTop < pRight) {
			direction = Direction.TOP;
			return top;
		}
		
		// Links
		if(pLeft < pRight) {
			direction = Direction.LEFT;
			return left;
		}
		
		// default, höchste Priorität
		direction = Direction.RIGHT;
		return right;
	}
}