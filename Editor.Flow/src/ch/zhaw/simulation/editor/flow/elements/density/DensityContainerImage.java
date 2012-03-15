package ch.zhaw.simulation.editor.flow.elements.density;

import java.awt.Graphics2D;
import java.awt.Polygon;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class DensityContainerImage extends GuiImage {

	public DensityContainerImage(int width, int height, GuiConfig config) {
		super(width, height, config, false);
	}

	@Override
	public void drawImage(Graphics2D g, boolean selected) {
		int w = getWidth();
		int h = getHeight();

		DrawHelper.antialisingOn(g);

		int rectPaddingTopBottom = w / 4;

		// most left
		int aX = 0;
		int aY = h - rectPaddingTopBottom;

		// topmost center
		int bX = w / 2;
		int bY = h - 1;

		// most right
		int cX = w - 1;
		int cY = aY;

		// most left
		int dX = aX;
		int dY = rectPaddingTopBottom;

		// "center, top"
		int eX = bX;
		int eY = 2 * rectPaddingTopBottom;

		int fX = cX;
		int fY = dY;

		// most top
		int gX = bX;
		int gY = 0;

		Polygon pA = new Polygon(); // left side
		pA.addPoint(aX, aY);
		pA.addPoint(bX, bY);
		pA.addPoint(eX, eY);
		pA.addPoint(dX, dY);

		Polygon pB = new Polygon(); // right side
		pB.addPoint(bX, bY);
		pB.addPoint(cX, cY);
		pB.addPoint(fX, fY);
		pB.addPoint(eX, eY);

		Polygon pC = new Polygon(); // top
		pC.addPoint(dX, dY);
		pC.addPoint(eX, eY);
		pC.addPoint(fX, fY);
		pC.addPoint(gX, gY);

		g.setPaint(config.getContainerPaint(w, h, selected));
		g.fill(pA);
		g.fill(pB);
		g.fill(pC);

		g.setPaint(config.getObjectBorder(selected));
		g.draw(pA);
		g.draw(pB);
		g.draw(pC);
	}
}
