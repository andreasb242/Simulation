package ch.zhaw.simulation.editor.connector.parameterarrow;


import java.awt.Graphics2D;
import java.awt.Paint;

import butti.javalibs.util.DrawHelper;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.gui.control.GuiConfig;


public class ConnectorPointImage extends GuiImage {

	public ConnectorPointImage(int width, int height, GuiConfig config) {
		super(width, height, config, false);
	}

	@Override
	protected void drawBackground(Graphics2D g, boolean selected) {
		int w = image.getWidth();
		Paint paint = config.getConnectorPaint(w, w, selected);
		Paint borderPaint = config.getConnectorBorderPaint(w, w, selected);

		DrawHelper.antialisingOff(g);

		g.setPaint(paint);
		g.fillRect(0, 0, w, w);

		g.setPaint(borderPaint);
		g.drawRect(0, 0, w - 1, w - 1);

		g.dispose();
	}
}
