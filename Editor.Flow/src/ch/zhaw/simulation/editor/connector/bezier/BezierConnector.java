package ch.zhaw.simulation.editor.connector.bezier;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.flow.connector.parameterarrow.BezierHelperPoint;
import ch.zhaw.simulation.model.flow.BezierConnectorData;

public abstract class BezierConnector {
	protected CubicCurve2D.Double curve = new CubicCurve2D.Double();

	protected ConnectorPositionCalculator start;
	protected BezierHelperPoint movePoint;
	protected ConnectorPositionCalculator end;

	protected JComponent parent;

	/**
	 * If this connector is selected
	 */
	protected boolean selected = false;

	public BezierConnector(JComponent parent, BezierConnectorData connector, AbstractEditorControl<?> control) {
		this.parent = parent;

		movePoint = new BezierHelperPoint(connector, control);
		start = new ConnectorPositionCalculator(connector.getSource(), movePoint);
		end = new ConnectorPositionCalculator(connector.getTarget(), movePoint);

		if (connector.getHelperPoint() == null) {
			centerMovePoint();
		}

		parent.add(movePoint);

		updateBezier();
	}

	public void centerMovePoint() {
		// Place the center Point
		centerMovePoint2();

		// Calculate the right position
		centerMovePoint2();
	}

	private void centerMovePoint2() {
		int x = getCenter(start.getPoint().x, end.getPoint().x);
		int y = getCenter(start.getPoint().y, end.getPoint().y);

		movePoint.setPosition(x, y);
	}

	public ConnectorPositionCalculator getStart() {
		return start;
	}

	public ConnectorPositionCalculator getEnd() {
		return end;
	}

	public BezierHelperPoint getMovePoint() {
		return movePoint;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	private int getCenter(int i1, int i2) {
		if (i1 < i2) {
			return (i2 - i1) / 2 + i1;
		} else {
			return (i1 - i2) / 2 + i2;
		}
	}

	protected void updateBezier() {

		Point startPoint = start.getPoint();
		Point endPoint = end.getPoint();
		Point middle = movePoint.getPoint();

		// TODO repaint wenn schnell bewegt!!

		this.curve.setCurve(startPoint.x, startPoint.y, middle.x, middle.y, middle.x, middle.y, endPoint.x, endPoint.y);
	}

	public Rectangle getBounds() {
		return this.curve.getBounds();
	}

	public boolean isConnector(Point point) {
		final int padding = 5;
		return curve.intersects(point.x - padding, point.y - padding, padding * 2, padding * 2);
	}

	public void draw(Graphics2D g) {
		g.draw(curve);
	}

	public static void drawArrow(Graphics2D g, double lastX1, double lastY1, double lastX2, double lastY2) {
		double x = lastX1 - lastX2;
		double y = lastY1 - lastY2;

		double angle = Math.PI / 8;
		double rad = Math.atan(x / y);

		int arrowLength = 20;

		if (y >= 0) {
			arrowLength = -arrowLength;
		}

		x = Math.sin(rad - angle) * arrowLength;
		y = Math.cos(rad - angle) * arrowLength;

		g.drawLine((int) (lastX2 - x), (int) (lastY2 - y), (int) lastX2, (int) lastY2);

		x = Math.sin(rad + angle) * arrowLength;
		y = Math.cos(rad + angle) * arrowLength;

		g.drawLine((int) (lastX2 - x), (int) (lastY2 - y), (int) lastX2, (int) lastY2);
	}

	public void paint(Graphics2D g) {
		updateBezier();
		DrawHelper.antialisingOn(g);

		drawArrow(g);
		drawCurve(g);
	}

	protected abstract void drawArrow(Graphics2D g);

	protected abstract void drawCurve(Graphics2D g);

	public void dispose() {
		parent.remove(movePoint);
		end.dispose();
		end = null;
		start.dispose();
		start = null;
		movePoint.dispose();
		movePoint = null;

	}
}
