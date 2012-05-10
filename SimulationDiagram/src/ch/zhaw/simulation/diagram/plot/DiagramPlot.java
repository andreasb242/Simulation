package ch.zhaw.simulation.diagram.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.diagram.DiagramConfigAdapter;
import ch.zhaw.simulation.diagram.DiagramConfigListener;
import ch.zhaw.simulation.diagram.DiagramConfigModel;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramPlot extends JComponent {
	private static final long serialVersionUID = 1L;

	private DiagramConfigModel model;

	private DiagramConfigListener listener = new DiagramConfigAdapter() {
		@Override
		public void serieDisabled(SimulationSerie s) {
			updateSimulationYRange();
		}

		@Override
		public void serieEnabled(SimulationSerie s) {
			updateSimulationYRange();
		}
	};

	private Raster raster = new Raster();
	private SeriePlot plot = new SeriePlot();
	
	private double yRangeMax;
	private double yRangeMin;
	private double xRangeMax;
	private double xRangeMin;

	public DiagramPlot(DiagramConfigModel model) {
		this.model = model;
		model.addListener(listener);

		updateSimulationYRange();
		updateSimulationXRange();
	}

	private void setScrollSize() {
		// Size of Scrollbar
		setPreferredSize(new Dimension((int) (xRangeMax - xRangeMin) + 30, (int) (yRangeMax - yRangeMin) + 30));
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = DrawHelper.antialisingOn(g1);

		int w = getWidth();
		int h = getHeight();

		// Background white
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, w, h);
		
		raster.paint(g);
		
		
		SimulationCollection collection = model.getCollection();
		for(SimulationSerie s : collection) {
			if(model.isEnabled(s)) {
				AffineTransform transform = g.getTransform();
				plot.plot(g, s);
				g.setTransform(transform);
			}
		}
	}

	private void updateSimulationXRange() {
		SimulationCollection collection = model.getCollection();
		xRangeMin = collection.getStartTime();
		xRangeMax = collection.getEndTime();
	}

	public void updateSimulationYRange() {
		SimulationCollection collection = model.getCollection();

		yRangeMin = Double.MAX_VALUE;
		yRangeMax = Double.MIN_VALUE;

		for (SimulationSerie s : collection) {
			if (s.getMin() < yRangeMin) {
				yRangeMin = s.getMin();
			}

			if (s.getMax() > yRangeMax) {
				yRangeMax = s.getMax();
			}
		}

		setScrollSize();
		repaint();
	}

	public void dispose() {
		model.removeListener(listener);
	}
}
