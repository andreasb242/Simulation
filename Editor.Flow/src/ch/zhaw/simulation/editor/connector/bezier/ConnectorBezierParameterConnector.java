package ch.zhaw.simulation.editor.connector.bezier;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.model.flow.BezierConnectorData;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class ConnectorBezierParameterConnector extends BezierConnector {

	private GuiConfig guicfg;

	public ConnectorBezierParameterConnector(JComponent parent, BezierConnectorData connector, AbstractConnectorData<?> connectorconnectorData,
			AbstractEditorControl<?> control) {
		super(parent, connector, connectorconnectorData, control);

		this.guicfg = control.getSysintegration().getGuiConfig();
	}

	@Override
	protected void drawArrow(Graphics2D g) {

		final double u = 0.96875;
		double x2;
		double y2;

		Point startPoint = start.getPoint();
		Point endPoint = end.getPoint();
		Point middle = movePoint.getPoint();

		x2 = startPoint.x * Math.pow(1.0 - u, 3) + middle.x * 3 * u * Math.pow(1.0 - u, 2) + middle.x * 3 * Math.pow(u, 2) * (1.0 - u) + endPoint.x
				* Math.pow(u, 3);

		y2 = startPoint.y * Math.pow(1.0 - u, 3) + middle.y * 3 * u * Math.pow(1.0 - u, 2) + middle.y * 3 * Math.pow(u, 2) * (1.0 - u) + endPoint.y
				* Math.pow(u, 3);

		g.setColor(Color.BLACK);
		drawArrow(g, x2, y2, endPoint.x, endPoint.y);
	}

	@Override
	protected void drawCurve(Graphics2D g) {
		g.setColor(guicfg.getConnectorLineColor(this.selected));
		g.draw(curve);
	}

}
