package ch.zhaw.simulation.diagram;

import java.util.Vector;

import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class DiagramConfigModel {

	private SimulationCollection collection;

	private Vector<DiagramConfigListener> listener = new Vector<DiagramConfigListener>();

	private Vector<SimulationSerie> enableSeries = new Vector<SimulationSerie>();

	public DiagramConfigModel(SimulationCollection collection) {
		this.collection = collection;
		if (collection == null) {
			throw new NullPointerException("collection == null");
		}
	}

	public void setLogXEnabled(boolean log) {
		for (DiagramConfigListener l : this.listener) {
			l.setLogXEnabled(log);
		}
	}

	public void setLogYEnabled(boolean log) {
		for (DiagramConfigListener l : this.listener) {
			l.setLogYEnabled(log);
		}
	}

	public SimulationCollection getCollection() {
		return collection;
	}

	public void addListener(DiagramConfigListener listener) {
		this.listener.add(listener);
	}

	public void removeListener(DiagramConfigListener listener) {
		this.listener.remove(listener);
	}

	public void enableSerie(SimulationSerie s) {
		if (enableSeries.contains(s)) {
			return;
		}

		enableSeries.add(s);

		for (DiagramConfigListener l : this.listener) {
			l.serieEnabled(s);
		}
	}

	public void disableSerie(SimulationSerie s) {
		if (!enableSeries.remove(s)) {
			return;
		}

		for (DiagramConfigListener l : this.listener) {
			l.serieDisabled(s);
		}
	}

	public boolean isEnabled(SimulationSerie s) {
		return enableSeries.contains(s);
	}

	public void enableSeries(String[] series) {
		Vector<String> names = new Vector<String>();
		for (String s : series) {
			names.add(s);
		}

		for (SimulationSerie s : collection) {
			// remove: same as contains, and the next search is faster, because
			// there is an an element less in the the list
			if (names.remove(s.getName())) {
				enableSeries.add(s);
			}
		}
	}

	/**
	 * @param series
	 *            The series to enable, seperated with comma, <code>null</code>
	 *            enables all
	 */
	public void enableSeries(String series) {
		// default: enable all
		if (series == null) {
			for (SimulationSerie c : collection) {
				enableSeries.add(c);
			}
		} else {
			enableSeries(series.split(","));
		}
	}

	public int getSelectionCount() {
		return enableSeries.size();
	}

	public String getEnabledSeriesString() {
		StringBuilder s = new StringBuilder();
		for (SimulationSerie e : enableSeries) {
			s.append(",");
			s.append(e.getName());
		}

		if (s.length() == 0) {
			return "";
		}

		return s.toString().substring(1);
	}

}
