package ch.zhaw.simulation.editor.xy.element.meso;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class MesoImage extends GuiImage {

	private GradientPaint paint;

	public MesoImage(int size, GuiConfig config) {
		super(size, size, config, false);
		setColor(Color.WHITE);
	}

	public void setColor(Color color) {
		int size = getHeight();
		paint = new GradientPaint(new Point2D.Float(0, 0), color, new Point2D.Float(0, size), color.darker());
	}

	@Override
	public void drawImage(Graphics2D g, boolean selected) {
		DrawHelper.antialisingOn(g);

		int size = getHeight();

		Arc2D.Float circle = new Arc2D.Float(1, 1, size - 2, size - 2, 0, 360, Arc2D.OPEN);

		g.setPaint(paint);

		g.fill(circle);

		g.setPaint(config.getObjectBorder(selected));
		g.draw(circle);
	}
}
