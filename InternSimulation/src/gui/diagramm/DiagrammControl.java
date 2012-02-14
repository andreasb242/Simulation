package gui.diagramm;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.Vector;

import simulation.data.SimulationCollection;
import simulation.data.SimulationEntry;
import simulation.data.SimulationSerie;

public class DiagrammControl {
	private SimulationCollection series;
	private SimulationSerie[] data;

	private DiagrammLayout layout = new DiagrammLayout();

	/**
	 * Diagramm Listener
	 */
	private Vector<DiagrammListener> listener = new Vector<DiagrammListener>();

	/**
	 * Die X-Achse des Diagrammes
	 */
	private Axis xaxis;

	/**
	 * Die Y-Achse des Diagrammes
	 */
	private Axis yaxis;

	/**
	 * Das Koordinatensystem
	 */
	private CoordinatePainter coordinates;

	/**
	 * Die Transformation vom Model auf das Diagarmms
	 */
	private AffineTransform transform = new AffineTransform();

	/**
	 * Automatisch neu skalieren
	 */
	private boolean scale;

	public DiagrammControl(SimulationCollection series) {
		this.series = series;
		this.data = series.getSeries();
		yaxis = new Axis();

		calcColors();
		calcMinMax();
		calcGlobalMinMax();

		xaxis = new Axis(series.getStartTime(), series.getEndTime());
		coordinates = new CoordinatePainter(series, transform, layout);
	}

	public SimulationCollection getSeries() {
		return series;
	}

	public SimulationSerie[] getData() {
		return data;
	}

	private void calcGlobalMinMax() {
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;

		for (SimulationSerie d : data) {
			if (!d.isVisibel()) {
				continue;
			}

			if (max < d.getMax()) {
				max = d.getMax();
			}
			if (min > d.getMin()) {
				min = d.getMin();
			}
		}

		series.setYMax(max);
		series.setYMin(min);
		yaxis.setMinMax(series.getYMin(), series.getYMax());
	}

	private void calcMinMax() {
		for (SimulationSerie d : data) {
			calcMinMax(d);
		}
	}

	private void calcMinMax(SimulationSerie d) {
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;

		if (d.isConstValue()) {
			d.setMin(d.getConstValue());
			d.setMax(d.getConstValue());
			return;
		}

		Vector<SimulationEntry> data = d.getData();
		for (SimulationEntry a : data) {
			if (max < a.value) {
				max = a.value;
			}
			if (min > a.value) {
				min = a.value;
			}
		}

		d.setMin(min);
		d.setMax(max);
	}

	private void calcColors() {
		int count = series.size();
		float step = 1.0f / count;
		float h = 0;

		for (SimulationSerie d : data) {
			Color c = Color.getHSBColor(h, 1, 1);
			d.setColor(c);
			h += step;
		}
	}

	/**
	 * Shows or hides a serie
	 * 
	 * @param id
	 *            The id of the Serie
	 * @param visibel
	 *            true to show
	 */
	public void setVisibel(int id, boolean visibel) {
		data[id].setVisibel(visibel);
		if (scale) {
			calcGlobalMinMax();
			calcTransform();
			fireScaleChanged();
		} else {
			fireVisibilityChanged(data[id]);
		}
	}

	private void fireVisibilityChanged(SimulationSerie serie) {
		for (DiagrammListener l : listener) {
			l.visibilityChanged(serie);
		}
	}

	private void fireScaleChanged() {
		for (DiagrammListener l : listener) {
			l.scaleChanged();
		}
	}

	public void addListener(DiagrammListener l) {
		listener.add(l);
	}

	public void removeListener(DiagrammListener l) {
		listener.remove(l);
	}

	public int getDataWidth() {
		return data[0].getData().size();
	}

	public Axis getXaxis() {
		return xaxis;
	}

	public Axis getYaxis() {
		return yaxis;
	}

	public DiagrammLayout getLayout() {
		return layout;
	}

	/**
	 * Gibt das Koordinatensystem zurück
	 */
	public CoordinatePainter getCoordinates() {
		return coordinates;
	}

	public void setSize(int width, int height) {
		xaxis.setLength(width);
		yaxis.setLength(height);

		xaxis.setPadding1(layout.getMarginLeft());
		xaxis.setPadding2(layout.getMarginRight());

		yaxis.setPadding1(layout.getMarginTop());
		yaxis.setPadding2(layout.getMarginBottom());

		calcTransform();

		coordinates.calcValues();
	}

	private void calcTransform() {
		double xFactor;
		double yFactor;
		double xcoord0;
		double ycoord0;

		xFactor = 1 / xaxis.getZoomFactor();
		xcoord0 = xaxis.calcPointFromModel(0.0);

		yFactor = 1 / yaxis.getZoomFactor();
		ycoord0 = yaxis.getLength() - yaxis.calcPointFromModel(0.0) - yaxis.getPadding1() - yaxis.getPadding2();

		System.out.println("xFactor: " + xFactor + "; xcoord0: " + xcoord0 + "; yFactor: " + yFactor + "; ycoord0: " + ycoord0);

		xcoord0 += layout.getMarginLeft();
		ycoord0 += layout.getMarginTop();

		transform.setTransform(xFactor, 0, 0, -yFactor, xcoord0, ycoord0);
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public void setAutoScale(boolean scale) {
		this.scale = scale;
		calcGlobalMinMax();
		calcTransform();
		fireScaleChanged();
	}
}
