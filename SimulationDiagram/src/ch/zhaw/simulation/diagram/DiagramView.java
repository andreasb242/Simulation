package ch.zhaw.simulation.diagram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Vector;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.model.xy.ColorCalculator;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramView extends JComponent {
	private static final long serialVersionUID = 1L;

	private SimulationCollection collection;

	public DiagramView() {
		// Grösse für Scrollbar
		setPreferredSize(new Dimension(200, 200));
	}

	@Override
	public void paint(Graphics g1) {
		SimulationSerie serie;
		Vector<SimulationEntry> entries;
		SimulationEntry entry;
		Graphics2D g;
		Color colors[];
		Path2D path;
		int i, k;
		double x, y;
		double ratioX, ratioY;
		boolean first;

		g = DrawHelper.antialisingOn(g1);

		int w = getWidth();
		int h = getHeight();

		// Background white
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		if (collection != null) {
			colors = ColorCalculator.calcColors(collection.size());
			ratioX = (collection.getEndTime() - collection.getStartTime()) / (double) w;
			System.out.println("Time: " + collection.getStartTime() + " =>" + collection.getEndTime());
			System.out.println("Size: " + collection.size());
			for (i = 0; i < collection.size(); i++) {
				g.setColor(colors[i]);
				path = new Path2D.Double();
				
				serie = collection.getSerie(i);
				ratioY = (serie.getMax() - serie.getMin()) / (double) h;
				entries = serie.getData();
				System.out.println("=== " + serie.getName() + " =========================");
				System.out.println("Min/Max: " + serie.getMin() + "/" + serie.getMax() + " === Ratio: " + ratioX + " x " + ratioY + " === Dimension: " + w + " x " + h);
				first = true;
				for (k = 0; k < entries.size(); k++) {
					entry = entries.get(k);
					x = entry.time / ratioX;
					y = (serie.getMax() - entry.value) / ratioY;
					System.out.println(" (" + entry.time + "/" + entry.value + ") => (" + x + "/" + y + ")");
					if (first) {
						first = false;
						path.moveTo(x, y);
					} else {
						path.lineTo(x, y);
					}
				}

				g.draw(path);
			}
		}

	}

	public void updateSimulationCollection(SimulationCollection collection) {
		this.collection = collection;
		repaint();
	}

}
