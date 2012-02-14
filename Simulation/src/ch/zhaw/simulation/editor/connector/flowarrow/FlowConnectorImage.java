package ch.zhaw.simulation.editor.connector.flowarrow;


import java.awt.Graphics2D;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.util.DrawHelper;


public class FlowConnectorImage extends GuiImage {
	public FlowConnectorImage(int size, GuiConfig config) {
		super(size, size, config, true);
	}

	@Override
	protected void drawBackground(Graphics2D g, boolean selected) {
		DrawHelper.antialisingOff(g);
		int size = image.getHeight();

		g.setPaint(config.getFlowParameterBackground(size, size, selected));
		g.fillRect(0, 0, size, size);

		g.setPaint(config.getFlowParameterBorder(size, size, selected));
		g.drawRect(0, 0, size - 1, size - 1);

		g.dispose();
	}
}