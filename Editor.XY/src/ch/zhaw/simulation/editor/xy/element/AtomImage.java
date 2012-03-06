package ch.zhaw.simulation.editor.xy.element;

import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class AtomImage extends GuiImage {
	private static final int EDGE_RADIUS = 5;

	public AtomImage(int size, GuiConfig config) {
		super(size, size, config, false);
	}

	@Override
	protected void drawBackground(Graphics2D g, boolean selected) {
		DrawHelper.antialisingOn(g);
		
		int size = image.getHeight();

		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0, 0, size - 1, size - 1, EDGE_RADIUS, EDGE_RADIUS);

		g.setPaint(config.getFlowParameterBackground(size, size, selected));

		g.fill(rect);

		g.setPaint(config.getObjectBorder(selected));
		g.draw(rect);

		g.dispose();
	}

}
