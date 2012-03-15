package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.connector.bezier.Direction;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.util.gui.ImageMirrow;

public class FlowArrowImage extends GuiImage {
	/**
	 * Right
	 */
	protected BufferedImage imageR;
	protected BufferedImage imageSelectedR;

	/**
	 * Top
	 */
	protected BufferedImage imageT;
	protected BufferedImage imageSelectedT;

	/**
	 * Bottom
	 */
	protected BufferedImage imageB;
	protected BufferedImage imageSelectedB;

	private int arrowDiameter;

	private Direction direction = Direction.LEFT;

	private ImageObserver dummyObserver = new ImageObserver() {

		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			return true;
		}
	};

	public FlowArrowImage(int size, GuiConfig config) {
		super(size, size, config, false, false);

		initImage();
	}

	protected void initImage() {
		// TODO alles zeichnen, nicht bilder verwenden !
		BufferedImage imageL = GuiImage.drawToImage(this);
		BufferedImage imageSelectedL = GuiImage.drawToImage(this, true);

		imageR = ImageMirrow.horizontalMirror(imageL);
		imageSelectedR = ImageMirrow.horizontalMirror(imageSelectedL);

		imageB = ImageMirrow.rotateImage(imageL, 270);
		imageSelectedB = ImageMirrow.rotateImage(imageSelectedL, 270);

		imageT = ImageMirrow.verticalMirror(imageB);
		imageSelectedT = ImageMirrow.verticalMirror(imageSelectedL);
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public void drawImage(Graphics2D g, boolean selected) {
		switch (direction) {
		case BOTTOM:
			if (selected) {
				g.drawImage(imageSelectedB, 0, 0, dummyObserver);
				return;
			}
			g.drawImage(imageB, 0, 0, dummyObserver);
			return;

		case LEFT:
			drawArrowLeft(g, selected);
			return;

		case RIGHT:
			if (selected) {
				g.drawImage(imageSelectedR, 0, 0, dummyObserver);
				return;
			}
			g.drawImage(imageR, 0, 0, dummyObserver);
			return;

		case TOP:
			if (selected) {
				g.drawImage(imageSelectedT, 0, 0, dummyObserver);
				return;
			}
			g.drawImage(imageT, 0, 0, dummyObserver);
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

		g.dispose();
	}

	public int getArrowWidth() {
		return arrowDiameter;
	}
}