package ch.zhaw.simulation.editor.flow.connector.parameterarrow;

import java.awt.Graphics2D;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class ConnectorPointImage extends GuiImage {

	public ConnectorPointImage(int width, int height, GuiConfig config) {
		super(width, height, config, false);
	}

	@Override
	protected void drawBackground(Graphics2D g, boolean selected) {
		int w = image.getWidth();

		DrawHelper.antialisingOff(g);

		g.setPaint(config.getConnectorPaint(w, w, selected));
		g.fillRect(0, 0, w, w);

		g.setPaint(config.getObjectBorder(selected));
		g.drawRect(0, 0, w - 1, w - 1);

		g.dispose();
	}
}
