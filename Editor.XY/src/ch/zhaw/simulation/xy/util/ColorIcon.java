package ch.zhaw.simulation.xy.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import butti.javalibs.util.DrawHelper;

public class ColorIcon implements Icon {
	private Color color;

	public ColorIcon() {
		this(Color.BLACK);
	}

	public ColorIcon(Color color) {
		this.color = color;
	}

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = DrawHelper.antialisingOn(g1);
		g.setColor(color);
		g.fillOval(x, y, getIconWidth(), getIconHeight());
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public int getIconWidth() {
		return 16;
	}

	@Override
	public int getIconHeight() {
		return 16;
	}
}