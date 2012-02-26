package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Point;
import java.util.Vector;

import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;


public class FlowCalculator {
	private ElementConnector source;
	private ElementConnector target;
	
	private Vector<Point> points = new Vector<Point>();
	
	private SimulationObject object1;
	private FlowValve object2;

	private int arrowWidth;
	private int arrowHeight;
	private int arrowSize;
	private boolean rotateEnd;
	
	public FlowCalculator(SimulationObject object, FlowConnector connector, int arrowWidth, int arrowHeight, int arrowSize) {
		this.object1 = object;
		this.object2 = connector.getValve();

		this.arrowWidth = arrowWidth;
		this.arrowHeight = arrowHeight;
		this.arrowSize = arrowSize;

		source = new ElementConnector(connector.getValve(), object);
		target = new ElementConnector(object, connector.getValve());
	}

	public ElementConnector getTarget() {
		return target;
	}
	
	public void calc() {
		points.clear();
		
		FlowPoint source = new FlowPoint(this.source.getPoint(), this.source.getDirection(), 0, 0);
		FlowPoint target = new FlowPoint(this.target.getPoint(), this.target.getDirection(), arrowWidth, arrowHeight);

		// Check range
		if(object1.intersects(object2)) {
			return;
		}
		
		rotateEnd = false;
		
		boolean toNear = false;
		
		int paddingX = Math.abs(object1.getXCenter() - object2.getXCenter()) - (object1.getWidth() + object2.getWidth()) / 2 - arrowWidth;
		int paddingY = Math.abs(object1.getYCenter() - object2.getYCenter()) - (object1.getHeight() + object2.getHeight()) / 2 - arrowHeight;
		
		int padding = Math.max(paddingX, paddingY);
		
		// TODO: low prio: Berechnung für den Abstand verbessern
		if(padding < arrowSize) {
			toNear = true;
		}
		
		boolean rightDown = object1.getYCenter() > object2.getYCenter();
		if(object1.getXCenter() < object2.getXCenter()) {
			rightDown = !rightDown;
		}
		
		CenterPoint center = new CenterPoint(source, this.source.getDirection(), target, this.target.getDirection());

		int hs = arrowSize / 2;
		
		addStartPoints(source.getPoint(), hs);
		
		Point p1 = new Point(center.targetXa, center.targetYa);
		Point p2 = null;
		if(center.twoPoints) {
			p2 = new Point(center.targetXb, center.targetYb);
		}

		if(!toNear) {
			addCenterPoint1(p1, p2, hs, this.source.getDirection(), this.target.getDirection(), rightDown);
		}

		addEndPoints(target.getPoint(), hs);

		if(!toNear) {
			addCenterPoint2(p1, p2, hs, this.source.getDirection(), this.target.getDirection(), rightDown);
		}
	}
	
	private void addCenterPoint1(Point p1, Point p2, int hs, Direction sd, Direction td, boolean rightDown) {
		if(p2 != null) {
			Point add = centerAdd(sd, td, hs, p1, p2, rightDown);
			points.add(new Point(p1.x + add.x, p1.y + add.y));
			points.add(new Point(p2.x + add.x, p2.y + add.y));
		} else {
			Point add = etchAdd(sd, td, hs);
			points.add(new Point(p1.x + add.x, p1.y + add.y));
		}
	}

	private Point centerAdd(Direction sd, Direction td, int hs, Point p1, Point p2, boolean rightDown) {
//		System.out.println("sd: " + sd + ", td: " + td);
//		System.out.println("->" + rightDown);

		if(isDirection(sd, td, Direction.RIGHT, Direction.LEFT)) {
			int x = hs;
			int y = hs;

			if(rightDown) {
				x *= -1;
			}

			return new Point(x, y);
		}
		if(isDirection(sd, td, Direction.BOTTOM, Direction.TOP)) {
			int x = hs;
			int y = hs;

			if(rightDown) {
				y *= -1;
			}

			return new Point(x, y);
		}
		return new Point();
	}

	private Point etchAdd(Direction sd, Direction td, int hs) {
		if(isDirection(sd, td, Direction.LEFT, Direction.TOP)) {
			return new Point(hs, hs);
		}
		
		if(isDirection(sd, td, Direction.RIGHT, Direction.TOP)) {
			rotateEnd = true;
			return new Point(-hs, hs);
		}
		if(isDirection(sd, td, Direction.LEFT, Direction.BOTTOM)) {
			rotateEnd = true;
			return new Point(-hs, hs);
		}

		return new Point();
	}
	
	private boolean isDirection(Direction sd, Direction td, Direction a, Direction b) {
		return (sd == a && td == b || sd == b && td == a);
	}
	
	private void addCenterPoint2(Point p1, Point p2, int hs, Direction sd, Direction td, boolean rightDown) {
		if(p2 != null) {
			Point add = centerAdd(sd, td, hs, p1, p2, rightDown);
			points.add(new Point(p2.x - add.x, p2.y - add.y));
			points.add(new Point(p1.x - add.x, p1.y - add.y));
		} else {
			Point add = etchAdd(sd, td, hs);
			points.add(new Point(p1.x - add.x, p1.y - add.y));
		}
	}
	
	private void addStartPoints(Point p, int hs) {
		if(source.getDirection().isHorizontal()) {
			points.add(new Point(p.x, p.y - hs));
			points.add(new Point(p.x, p.y + hs));
		} else {
			points.add(new Point(p.x - hs, p.y));
			points.add(new Point(p.x + hs, p.y));
		}

	}
	
	private void addEndPoints(Point p, int hs) {
		Point p1 = null;
		Point p2 = null;
		
		if(this.target.getDirection().isHorizontal()) {
			p1 = new Point(p.x, p.y + hs);
			p2 = new Point(p.x, p.y - hs);
		} else {
			p1 = new Point(p.x + hs, p.y);
			p2 = new Point(p.x - hs, p.y);
		}
		
		if(rotateEnd) {
			points.add(p2);
			points.add(p1);
		} else {
			points.add(p1);
			points.add(p2);
		}
	}

	public Vector<Point> getPoints() {
		return points;
	}

	public void dispose() {
		points.clear();

		source.dispose();
		target.dispose();

		source = null;
		target = null;
	}
	

	private class CenterPoint {
		public int targetXa = 0;
		public int targetYa = 0;

		public int targetXb = 0;
		public int targetYb = 0;

		public int centerX = 0;
		public int centerY = 0;
		
		public boolean twoPoints = true;
		
		public CenterPoint(FlowPoint source, Direction sd, FlowPoint target, Direction dt) {
			if(sd.isHorizontal() == dt.isHorizontal()) {
				initLine(source, sd, target, dt);
			} else {
				initEdge(source, sd, target, dt);
			}
		}
		
		private void initLine(FlowPoint source, Direction sd, FlowPoint target, Direction dt) {
			if(sd.isHorizontal()) {
				centerX = center(source.targetX, target.targetX);

				targetXa = targetXb = centerX;

				targetYb = target.targetY;
				targetYa = source.targetY;
				
				centerY = center(targetYa, targetYb);
			} else {
				centerY = center(source.targetY, target.targetY);

				targetYb = targetYa = centerY;

				targetXb = target.targetX;
				targetXa = source.targetX;
				
				centerX = center(targetXa, targetXb);
			}
		}

		private void initEdge(FlowPoint source, Direction sd, FlowPoint target, Direction dt) {
			twoPoints = false;
			
			if(source.targetX > target.targetX) {
				if(source.targetY > target.targetY) {
					targetXa = target.targetX;
					targetYa = source.targetY;
				} else {
					targetXa = target.targetX;
					targetYa = source.targetY;
				}
			} else {
				if(source.targetY > target.targetY) {
					targetXa = source.targetX;
					targetYa = target.targetY;
				} else {
					targetXa = target.targetX;
					targetYa = source.targetY;
				}
			}

			centerX = targetXa;
			centerY = targetYa;
		}

		private int center(int i1, int i2) {
			return (i1 + i2) / 2;
		}
	}

	private class FlowPoint {
		public int targetX = 0;
		public int targetY = 0;

		
		public FlowPoint(Point targetPoint, Direction direction, int h, int w) {
			switch(direction) {
			case BOTTOM:
				targetY = targetPoint.y + h;
				targetX = targetPoint.x;
				break;
			case LEFT:
				targetX = targetPoint.x - w;
				targetY = targetPoint.y;
				break;
			case RIGHT:
				targetX = targetPoint.x + w;
				targetY = targetPoint.y;
				break;
			case TOP:
				targetY = targetPoint.y - h;
				targetX = targetPoint.x;
				break;
			}
		}


		public Point getPoint() {
			return new Point(targetX, targetY);
		}
	}
}
