package ch.zhaw.simulation.editor.elements.global;


import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.util.DrawHelper;


public class GlobalImage extends GuiImage {

	public GlobalImage(int size, GuiConfig config) {
		super(size, size, config, true);
	}

	@Override
	protected void drawBackground(Graphics2D g, boolean selected) {
		int w = image.getWidth();

		DrawHelper.antialisingOn(g);

		Arc2D.Float circle = new Arc2D.Float(1, 1, w - 2, w - 2, 0, 360, Arc2D.OPEN);

		g.setPaint(config.getGlobalPaint(image.getWidth(), image.getHeight(), selected));
		g.fill(circle);

		g.setPaint(config.getObjectBorder(selected));
		g.draw(circle);

		g.dispose();
	}
}
