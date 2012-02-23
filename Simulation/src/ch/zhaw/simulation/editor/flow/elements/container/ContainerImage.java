package ch.zhaw.simulation.editor.flow.elements.container;


import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

import butti.javalibs.util.DrawHelper;

import ch.zhaw.simulation.editor.flow.elements.GuiImage;
import ch.zhaw.simulation.gui.control.GuiConfig;


public class ContainerImage extends GuiImage {
	public ContainerImage(int width, int heigth, GuiConfig config) {
		super(width, heigth, config, true);
	}

	@Override
	protected void drawBackground(Graphics2D g, boolean selected) {
		int w = image.getWidth();
		int h = image.getHeight();

		int circleHeight = w / 2;

		DrawHelper.antialisingOn(g);

		g.setPaint(config.getObjectBorder(selected));

		int x1 = 1;
		int y1 = 1 + (circleHeight / 2);
		int x2 = w - 2;
		int y2 = h - circleHeight - 2;

		DrawHelper.antialisingOff(g);
		Rectangle2D.Double rect = new Rectangle2D.Double(x1, y1, x2, y2);
		g.setPaint(config.getContainerPaint(w, h, selected));
		g.fill(rect);
		g.setPaint(config.getObjectBorder(selected));
		g.drawLine(x1 - 1, y1, x1 - 1, y2 + y1);// TODO: neues antialising -1
		g.drawLine(x2, y1, x2, y2 + y1);
		DrawHelper.antialisingOn(g);

		int cEndY = h - circleHeight - 2;

		Arc2D.Double circleTop = new Arc2D.Double(1, 1, w - 2, circleHeight, 0, 360, Arc2D.OPEN);
		g.setPaint(config.getContainerPaintTop(w, h, selected));
		g.fill(circleTop);
		g.setPaint(config.getObjectBorder(selected));
		g.draw(circleTop);

		// TODO: neues antialising w - 3, war w - 2
		Arc2D.Double circleBottom = new Arc2D.Double(1, cEndY, w - 3, circleHeight, 180, 180, Arc2D.OPEN);
		g.setPaint(config.getContainerPaint(w, h, selected));
		g.fill(circleBottom);
		g.setPaint(config.getObjectBorder(selected));
		g.draw(circleBottom);

		g.dispose();
	}
}
