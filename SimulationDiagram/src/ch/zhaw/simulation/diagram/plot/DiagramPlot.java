package ch.zhaw.simulation.diagram.plot;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.diagram.DiagramConfigAdapter;
import ch.zhaw.simulation.diagram.DiagramConfigListener;
import ch.zhaw.simulation.diagram.DiagramConfigModel;
import ch.zhaw.simulation.diagram.DiagramConfiguration;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramPlot extends JComponent {
	private static final long serialVersionUID = 1L;

	private DiagramConfigModel model;
	private ZoomAndPositionHandler zoom;

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

	private Raster raster;
	private HashMap<SimulationSerie, SeriePlot> plots = new HashMap<SimulationSerie, SeriePlot>();

	/**
	 * The maximum Y value (not scaled)
	 */
	private double yRangeMax;

	/**
	 * The minimum Y value (not scaled)
	 */
	private double yRangeMin;

	/**
	 * The maximum X value (not scaled)
	 */
	private double xRangeMax;

	/**
	 * The minimum X value (not scaled)
	 */
	private double xRangeMin;

	private ActionListener zoomListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
		}
	};

	public DiagramPlot(DiagramConfigModel model, ZoomAndPositionHandler zoom, DiagramConfiguration config) {
		this.model = model;
		this.zoom = zoom;
		model.addListener(listener);

		this.raster = new Raster(zoom, config);

		updateSimulationXRange();
		updateSimulationYRange();

		this.zoom.addListener(zoomListener);
	}

	private void setScrollSize() {
		// Size of Scrollbar
		
		// TODO !! SIZE
		setPreferredSize(new Dimension((int) (xRangeMax - xRangeMin) + 30, (int) (yRangeMax - yRangeMin) + 30));
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = DrawHelper.antialisingOn(g1);

		raster.paint(g);

		SimulationCollection collection = model.getCollection();
		for (SimulationSerie s : collection) {
			if (model.isEnabled(s)) {
				SeriePlot plot = getPlotFor(s);
				plot.plot(g);
			}
		}
	}

	private SeriePlot getPlotFor(SimulationSerie s) {
		SeriePlot plot = plots.get(s);
		if (plot == null) {
			plot = new SeriePlot(s, zoom);
			plots.put(s, plot);
		}
		return plot;
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

		zoom.setDataOffsetY(yRangeMax);

		raster.size(xRangeMin, xRangeMax, yRangeMin, yRangeMax);

		setScrollSize();
		repaint();
	}

	public void dispose() {
		model.removeListener(listener);
		zoom.removeListener(zoomListener);

		for (SeriePlot p : this.plots.values()) {
			p.dispose();
		}
	}
}
