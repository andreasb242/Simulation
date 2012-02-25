package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.flow.connector.ConnectorUi;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowConnectorControl.FlowControlListener;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionListener;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class FlowConnectorUi implements ConnectorUi, FlowControlListener,
		SelectionListener {
	private FlowConnectorControl flowControl;

	private FlowArrowImage arrowImage;
	private JComponent parent;

	private FlowConnector connector;
	private GuiConfig config;

	private FlowConnectorParameter connectorControl;

	private FlowEditorControl control;

	private boolean selected = false;

	public FlowConnectorUi(JComponent parent, FlowConnector connector,
			FlowEditorControl control, FlowConnectorParameter connectorControl) {
		this.parent = parent;
		this.connectorControl = connectorControl;
		this.connector = connector;
		this.config = control.getSysintegration().getGuiConfig();
		this.control = control;

		int arrowSize = config.getFlowArrowSize();
		arrowImage = new FlowArrowImage(arrowSize, config);

		flowControl = new FlowConnectorControl(connector, control,
				arrowImage.getArrowWidth());

		flowControl.addListener(this);

		control.getSelectionModel().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		parent = null;
		control.getSelectionModel().removeSelectionListener(this);
		control = null;
		flowControl.removeListener(this);
		flowControl.dispose();
		flowControl = null;
		connector = null;
		connectorControl = null;
	}

	@Override
	public FlowConnector getData() {
		return connector;
	}

	@Override
	public void paint(Graphics2D g) {
		ElementConnector target = flowControl.getTarget();
		Point targetPoint = target.getPoint();
		Direction direction = target.getDirection();

		BufferedImage arrow = arrowImage.getImage(selected, direction);

		int w = arrow.getWidth();
		int h = arrow.getHeight();

		int x = targetPoint.x;
		int y = targetPoint.y;

		switch (direction) {
		case BOTTOM:
			x -= w / 2;
			break;
		case LEFT:
			x -= w;
			y -= h / 2;
			break;
		case RIGHT:
			y -= h / 2;
			break;
		case TOP:
			x -= w / 2;
			y -= h;
			break;
		}

		g.drawImage(arrow, x, y, parent);

		DrawHelper.antialisingOff(g);

		drawLine(g, flowControl.getFlow1().getPoints());
		drawLine(g, flowControl.getFlow2().getPoints());

		g.setPaint(config.getFlowArrowFill(selected));

		int s = arrowImage.getArrowWidth();

		switch (direction) {
		case LEFT:
			y += (h - s) / 2;
			g.drawLine(x, y + 1, x, y + s - 1);

			// TODO: Übergang zwischen FlowPoint und Flow links ohne zusätzliche
			// Linie lösen
			break;
		case RIGHT:
			y = y + (h - s) / 2;
			x += arrow.getWidth();
			g.drawLine(x - 100, y + 1, x, y + s - 1);
			break;
		case TOP:
			x += (w - s) / 2;
			g.drawLine(x + 1, y, x + s - 1, y);
			break;
		case BOTTOM:
			y += arrow.getHeight();
			x += (w - s) / 2;
			g.drawLine(x + 1, y, x + s - 1, y);
			break;
		}
	}

	private void drawLine(Graphics2D g, Vector<Point> points) {
		Polygon flow = new Polygon();

		Point p;

		for (int i = 0; i < points.size(); i++) {
			p = points.get(i);
			flow.addPoint(p.x, p.y);
		}

		g.setPaint(config.getFlowArrowFill(selected));
		g.fill(flow);

		g.setPaint(config.getFlowArrowBorder(selected));
		g.draw(flow);
	}

	@Override
	public void repaint(int x, int y, int width, int height) {
		parent.repaint(x, y, width, height);
	}

	@Override
	public SelectableElement getSelectableElement() {
		return connectorControl;
	}

	private boolean isInside(Point point, Vector<Point> points) {
		Polygon flow = new Polygon();
		Point p;

		for (int i = 0; i < points.size(); i++) {
			p = points.get(i);
			flow.addPoint(p.x, p.y);
		}

		return flow.contains(point);
	}

	@Override
	public boolean isConnector(Point point) {
		if (isInside(point, flowControl.getFlow1().getPoints())) {
			return true;
		}
		if (isInside(point, flowControl.getFlow2().getPoints())) {
			return true;
		}

		return false;
	}

	@Override
	public void selectionChanged() {
		this.selected = control.getSelectionModel()
				.isSelected(connectorControl);
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}
}
