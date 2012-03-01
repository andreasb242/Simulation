package ch.zhaw.simulation.editor.flow.connector.parameterarrow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;

public class ParameterConnectorUi implements ConnectorUi, SelectionListener {
	private ElementConnectorParameter start;
	private ConnectorPoint movePoint;
	private ElementConnectorParameter end;

	private Color color;
	private Color colorSelected;
	private ParameterConnector data;
	private JComponent parent;

	private CubicCurve2D.Double connecorCurve;

	private FlowEditorControl control;

	private Point lastStartPoint;
	private Point lastEndPoint;
	private Point lastMiddle;

	private boolean selected = false;

	public ParameterConnectorUi(JComponent parent, ParameterConnector c, FlowEditorControl control) {
		this.parent = parent;
		this.control = control;
		this.data = c;
		color = control.getSysintegration().getGuiConfig().getConnectorLineColor();
		colorSelected = control.getSysintegration().getGuiConfig().getConnectorLineColorSelected();

		start = new ElementConnectorParameter(c.getSource(), this);
		end = new ElementConnectorParameter(c.getTarget(), this);

		movePoint = new ConnectorPoint(c, control, this);

		if (c.getConnectorPoint() == null) {
			centerMovePoint();
		}

		parent.add(movePoint);
		control.getSelectionModel().addSelectionListener(this);

		calcCurve();
	}

	@Override
	public boolean isConnector(Point point) {
		final int padding = 5;
		return connecorCurve.intersects(point.x - padding, point.y - padding, padding * 2, padding * 2);
	}

	private void calcCurve() {
		Point startPoint = start.getPoint();
		Point endPoint = end.getPoint();
		Point middle = movePoint.getPoint();

		if (startPoint.equals(lastStartPoint) && endPoint.equals(lastEndPoint) && middle.equals(lastMiddle)) {
			return;
		}

		lastStartPoint = startPoint;
		lastEndPoint = endPoint;
		lastMiddle = middle;

		connecorCurve = new CubicCurve2D.Double(startPoint.x, startPoint.y, middle.x, middle.y, middle.x, middle.y, endPoint.x, endPoint.y);
	}

	public void dispose() {
		control.getSelectionModel().removeSelectionListener(this);

		parent.remove(movePoint);
		parent = null;
		data = null;
		end.dispose();
		end = null;
		start.dispose();
		start = null;
		movePoint.dispose();
		movePoint = null;

		control = null;
	}

	public ParameterConnector getData() {
		return data;
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

	private int getCenter(int i1, int i2) {
		if (i1 < i2) {
			return (i2 - i1) / 2 + i1;
		} else {
			return (i1 - i2) / 2 + i2;
		}
	}

	public ElementConnectorParameter getStart() {
		return start;
	}

	public ElementConnectorParameter getEnd() {
		return end;
	}

	public ConnectorPoint getMovePoint() {
		return movePoint;
	}

	public void paint(Graphics2D g) {
		if (selected) {
			g.setColor(colorSelected);
		} else {
			g.setColor(color);
		}

		DrawHelper.antialisingOn(g);

		Point startPoint = start.getPoint();
		Point endPoint = end.getPoint();
		Point middle = movePoint.getPoint();
		calcCurve();

		g.draw(connecorCurve);

		final double u = 0.96875;
		double x2;
		double y2;

		x2 = startPoint.x * Math.pow(1.0 - u, 3) + middle.x * 3 * u * Math.pow(1.0 - u, 2) + middle.x * 3 * Math.pow(u, 2) * (1.0 - u) + endPoint.x
				* Math.pow(u, 3);

		y2 = startPoint.y * Math.pow(1.0 - u, 3) + middle.y * 3 * u * Math.pow(1.0 - u, 2) + middle.y * 3 * Math.pow(u, 2) * (1.0 - u) + endPoint.y
				* Math.pow(u, 3);

		drawArrow(g, x2, y2, endPoint.x, endPoint.y);
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

	@Override
	public void selectionChanged() {
		this.selected = control.getSelectionModel().isSelected(movePoint);
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}

	@Override
	public SelectableElement getSelectableElement() {
		return movePoint;
	}
}

class ElementConnectorParameter {
	private SimulationObject object;
	private ParameterConnectorUi connector;

	public ElementConnectorParameter(SimulationObject object, ParameterConnectorUi connector) {
		this.object = object;
		this.connector = connector;

		if (object == null) {
			throw new NullPointerException("object == null");
		}
		if (connector == null) {
			throw new NullPointerException("connector == null");
		}
	}

	public void dispose() {
		connector = null;
		object = null;

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
		Point movePointPosition = connector.getMovePoint().getPoint();

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
			return bottom;
		}

		if (pTop < pLeft && pTop < pRight) {
			return top;
		}

		if (pLeft < pRight) {
			return left;
		}

		// default, höchste Priorität
		return right;
	}
}
