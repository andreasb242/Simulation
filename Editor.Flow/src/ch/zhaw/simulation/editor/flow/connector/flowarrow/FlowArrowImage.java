package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.connector.bezier.Direction;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class FlowArrowImage extends GuiImage {
	private int arrowDiameter;

	private Direction direction = Direction.LEFT;

	public FlowArrowImage(int size, GuiConfig config) {
		super(size, size, config, false, false);
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void drawImage(Graphics2D g, boolean selected) {
		switch (direction) {
		case BOTTOM:
			drawArrowBottom(g, selected);
			return;

		case LEFT:
			drawArrowLeft(g, selected);
			return;

		case RIGHT:
			drawArrowRight(g, selected);
			return;

		case TOP:
			drawArrowTop(g, selected);
			return;
		}
		throw new RuntimeException("Unknown Direction: " + direction);
	}

	protected void drawArrowLeft(Graphics2D g, boolean selected) {
		int w = getWidth();
		arrowDiameter = w / 3;
		int circleWidth = w / 3;
		int circleStart = w / 10;
		int arrowWidth = circleStart + circleWidth;

		DrawHelper.antialisingOn(g);

		Arc2D.Float leftCircle = new Arc2D.Float(circleStart, 1, circleWidth - 2, w - 2, 90, 180, Arc2D.OPEN);

		Arc2D.Float rightCircle = new Arc2D.Float(circleStart, 1, circleWidth - 2, w - 2, -90, 180, Arc2D.OPEN);

		Arc2D.Float circle = new Arc2D.Float(circleStart, 1, circleWidth - 2, w - 2, 0, 360, Arc2D.OPEN);

		g.setPaint(config.getFlowArrowBackground(circleWidth - 2, w));

		g.fill(circle);

		g.setPaint(config.getConnectorLineColor(selected));
		g.draw(leftCircle);

		g.setPaint(config.getConnectorLineColor(selected));

		int y1 = (w - arrowDiameter) / 2;

		g.setPaint(config.getFlowLineColor(selected));
		g.fillRect(0, y1 + 1, arrowWidth, arrowDiameter - 1);

		g.setPaint(config.getFlowArrowForeground(circleWidth - 2, w));

		Polygon triangle = new Polygon();
		triangle.addPoint(w - 1, w / 2 - 1);
		triangle.addPoint(circleStart + circleWidth / 2, 1);
		triangle.addPoint(circleStart + circleWidth / 2, w - 1);
		triangle.addPoint(w - 1, w / 2);

		Area arrow = new Area(triangle);
		arrow.subtract(new Area(rightCircle));
		g.fill(arrow);

		g.setPaint(config.getConnectorLineColor(selected));
		g.draw(arrow);
		g.draw(rightCircle);
	}

	protected void drawArrowRight(Graphics2D g, boolean selected) {
		int w = getWidth();

		AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
		transform.translate(-w, 0);
		g.transform(transform);

		drawArrowLeft(g, selected);
	}

	protected void drawArrowTop(Graphics2D g, boolean selected) {
		int w = getWidth();

		AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
		transform.translate(-w, 0);
		transform.quadrantRotate(1);
		transform.translate(0, -w);
		g.transform(transform);

		drawArrowLeft(g, selected);
	}

	protected void drawArrowBottom(Graphics2D g, boolean selected) {
		int h = getHeight();

		g.translate(0, h);
		g.transform(AffineTransform.getQuadrantRotateInstance(-1));
		drawArrowLeft(g, selected);
	}

	public int getArrowWidth() {
		return arrowDiameter;
	}
}