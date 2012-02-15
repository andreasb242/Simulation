package ch.zhaw.simulation.editor.connector.flowarrow;


import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import butti.javalibs.util.DrawHelper;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.util.ImageMirrow;


public class FlowArrowImage extends GuiImage {
	protected BufferedImage imageR;
	protected BufferedImage imageSelectedR;

	protected BufferedImage imageT;
	protected BufferedImage imageSelectedT;

	protected BufferedImage imageB;
	protected BufferedImage imageSelectedB;

	private int arrowDiameter;

	public FlowArrowImage(int size, GuiConfig config) {
		super(size, size, config, false, false);
	}

	@Override
	protected void initImage() {
		super.initImage();
		imageR = ImageMirrow.horizontalMirror(image);
		imageSelectedR = ImageMirrow.horizontalMirror(imageSelected);
		putImg("imageR", imageR);
		putImg("imageSelectedR", imageSelectedR);

		imageB = ImageMirrow.rotateImage(image, 270);
		imageSelectedB = ImageMirrow.rotateImage(imageSelected, 270);
		putImg("imageB", imageB);
		putImg("imageSelectedB", imageSelectedB);

		imageT = ImageMirrow.verticalMirror(imageB);
		imageSelectedT = ImageMirrow.verticalMirror(imageSelected);
		putImg("imageT", imageT);
		putImg("imageSelectedT", imageSelectedT);
	}

	@Override
	protected void loadImages() {
		super.loadImages();

		imageR = getImg("imageR");
		imageSelectedR = getImg("imageSelectedR");

		imageB = getImg("imageB");
		imageSelectedB = getImg("imageSelectedB");

		imageT = getImg("imageT");
		imageSelectedT = getImg("imageSelectedT");
	}

	@Override
	protected void drawBackground(Graphics2D g, boolean selected) {
		int w = image.getWidth();
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

		g.setPaint(config.getFlowArrowBorder(selected));
		g.draw(leftCircle);

		g.setPaint(config.getFlowArrowBorder(selected));

		int y1 = (w - arrowDiameter) / 2;

		DrawHelper.antialisingOff(g);

		g.drawLine(0, y1, arrowWidth, y1);
		g.drawLine(0, y1 + arrowDiameter, arrowWidth, y1 + arrowDiameter);

		DrawHelper.antialisingOn(g);
		
		g.setPaint(config.getFlowArrowFill(selected));

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

		g.setPaint(config.getFlowArrowBorder(selected));
		g.draw(arrow);
		g.draw(rightCircle);

		g.dispose();
	}

	public int getArrowWidth() {
		return arrowDiameter;
	}

	public BufferedImage getImage(boolean selected, Direction direction) {
		switch (direction) {
		case BOTTOM:
			if (selected) {
				return imageSelectedB;
			}
			return imageB;

		case LEFT:
			if (selected) {
				return imageSelected;
			}
			return image;

		case RIGHT:
			if (selected) {
				return imageSelectedR;
			}
			return imageR;

		case TOP:
			if (selected) {
				return imageSelectedT;
			}
			return imageT;
		}
		throw new RuntimeException("Unknown Direction: " + direction);
	}
}