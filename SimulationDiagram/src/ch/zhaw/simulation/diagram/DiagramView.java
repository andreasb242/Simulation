package ch.zhaw.simulation.diagram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;

public class DiagramView extends JComponent {
	private static final long serialVersionUID = 1L;

	public DiagramView() {
		// Grösse für Scrollbar
		setPreferredSize(new Dimension(200, 200));
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = DrawHelper.antialisingOn(g1);

		int w = getWidth();
		int h = getHeight();

		// Background white
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		// Variante 1 (warscheinlich einfacher für uns)
		g.setColor(Color.BLACK);
		g.drawLine(0, 0, 100, 30);
		g.drawLine(100, 30, 150, 40);
		g.drawLine(150, 40, 200, 60);

		// Variante 2
		g.setColor(Color.GREEN);
		Polygon p = new Polygon();
		p.addPoint(5, 5);
		p.addPoint(10, 10);
		p.addPoint(20, 15);
		p.addPoint(30, 10);
		p.addPoint(40, 30);

		g.draw(p);

	}

}
