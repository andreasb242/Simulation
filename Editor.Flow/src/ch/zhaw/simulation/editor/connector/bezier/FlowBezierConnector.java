package ch.zhaw.simulation.editor.connector.bezier;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.model.flow.BezierConnectorData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class FlowBezierConnector extends BezierConnector {
	private GuiConfig guicfg;
	private boolean showArrow;
	private FlowArrowImage img;
	private int arrowSize;

	public FlowBezierConnector(JComponent parent, BezierConnectorData connector, AbstractEditorControl<?> control, boolean showArrow) {
		super(parent, connector, control);

		this.guicfg = control.getSysintegration().getGuiConfig();
		this.showArrow = showArrow;

		this.arrowSize = guicfg.getFlowArrowSize();

		img = new FlowArrowImage(arrowSize, guicfg);
	}

	@Override
	protected void drawArrow(Graphics2D g) {
		if (!this.showArrow) {
			return;
		}

		Direction anchor = end.getAnchorDirection();

		Point p = end.getPoint();
		int x = p.x;
		int y = p.y;

		if (anchor == Direction.LEFT) {
			x -= arrowSize;
		} else if (anchor == Direction.RIGHT) {
			x += arrowSize;
		} else if (anchor == Direction.TOP) {
			y -= arrowSize;
		} else if (anchor == Direction.BOTTOM) {
			y += arrowSize;
		}

		double ctrlx = curve.getCtrlX1();
		double ctrly = curve.getCtrlY1();
		curve.setCurve(curve.getX1(), curve.getY1(), ctrlx, ctrly, ctrlx, ctrly, x, y);

		if (anchor == Direction.LEFT) {
			y -= arrowSize / 2;
		} else if (anchor == Direction.RIGHT) {
			y -= arrowSize / 2;
			x -= arrowSize;
		} else if (anchor == Direction.TOP) {
			x -= arrowSize / 2;
		} else if (anchor == Direction.BOTTOM) {
			x -= arrowSize / 2;
			y -= arrowSize;
		}

		BufferedImage img = this.img.getImage(selected, anchor);
		g.drawImage(img, x, y, parent);
	}

	@Override
	protected void drawCurve(Graphics2D g) {
		Color c = guicfg.getFlowLineColor(selected);
		g.setPaint(c);
		Stroke strokeBackup = g.getStroke();
		g.setStroke(new BasicStroke(5));

		g.draw(this.curve);

		g.setStroke(strokeBackup);
	}
}