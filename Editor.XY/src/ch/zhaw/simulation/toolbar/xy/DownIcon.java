package ch.zhaw.simulation.toolbar.xy;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.swing.Icon;

import butti.javalibs.util.DrawHelper;

public class DownIcon implements Icon {

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(Color.BLACK);

		y += 9;

		Polygon p = new Polygon();
		p.addPoint(x + 0, y + 0);
		p.addPoint(x + 3, y + 7);
		p.addPoint(x + 6, y + 0);
		p.addPoint(x + 0, y + 0);

		Graphics2D g2 = DrawHelper.antialisingOn(g);
		g2.fill(p);
	}

	@Override
	public int getIconWidth() {
		return 6;
	}

	@Override
	public int getIconHeight() {
		return 24;
	}

}
