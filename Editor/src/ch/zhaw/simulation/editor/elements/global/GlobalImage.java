package ch.zhaw.simulation.editor.elements.global;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import butti.javalibs.util.DrawHelper;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class GlobalImage extends GuiImage {

	public GlobalImage(int size, GuiConfig config) {
		super(size, size, config, true);
	}

	@Override
	public void drawImage(Graphics2D g, boolean selected) {
		int w = getWidth();

		DrawHelper.antialisingOn(g);

		Arc2D.Float circle = new Arc2D.Float(1, 1, w - 2, w - 2, 0, 360, Arc2D.OPEN);

		g.setPaint(config.getGlobalPaint(w, getHeight(), selected));
		g.fill(circle);

		g.setPaint(config.getObjectBorder(selected));
		g.draw(circle);
	}
}
