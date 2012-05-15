package ch.zhaw.simulation.diagram;

import java.awt.Color;
import java.util.Vector;

import ch.zhaw.simulation.diagram.DiagramConfigListener.Direction;
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

	public void setLogEnabled(Direction direction, boolean log) {
		for (DiagramConfigListener l : this.listener) {
			l.setLogEnabled(direction, log);
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

	public SimulationSerie getSerieByName(String name) {
		if (name == null) {
			return null;
		}

		for (SimulationSerie s : collection) {
			if (name.equals(s.getName())) {
				return s;
			}
		}
		return null;
	}

	public void parseSeriesConfigString(String s) {
		// TODO: default!

		if (s == null || "".equals(s)) {
			return;
		}

		for (String e : s.split(",")) {
			String[] values = e.split(":");

			SimulationSerie serie = getSerieByName(values[0]);
			if (serie == null) {
				continue;
			}

			for (int i = 1; i < values.length ; i += 2) {
				String[] tmp = values[i].split("=");
				
				String key = tmp[0];
				String value = tmp[1];
				
				if ("color".equals(key) && "0x".equals(value.substring(0, 2))) {
					serie.setPaint(new Color(Integer.parseInt(value.substring(2), 16)));
				}
			}
		}
	}

	public String getSeriesConfigString() {
		StringBuilder s = new StringBuilder();
		for (SimulationSerie e : enableSeries) {
			s.append(",");
			s.append(e.getName());
			if (e.getPaint() != null) {
				s.append(":color=0x");
				s.append(HtmlColorHelper.getColorHex((Color) e.getPaint()));
			}
		}

		if (s.length() == 0) {
			return "";
		}

		return s.toString().substring(1);
	}
}
