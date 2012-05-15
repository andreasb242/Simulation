package ch.zhaw.simulation.diagram.persist;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Map.Entry;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class PersistDiagramSettings {

	private SimulationCollection collection;
	private XYLineAndShapeRenderer renderer;

	public PersistDiagramSettings(SimulationCollection collection, XYLineAndShapeRenderer renderer) {
		this.collection = collection;
		this.renderer = renderer;
	}

	private SimulationSerie getCollectionByName(String name) {
		for (SimulationSerie s : this.collection) {
			if (name.equals(s.getName())) {
				return s;
			}
		}
		return null;
	}

	// ///////////////////////////////////////////////
	// / Enabled
	// ///////////////////////////////////////////////
	private void parseSeriesEnabled(String data) {
		PersistListBoolean list = new PersistListBoolean("visibility");
		if (!list.parse(data)) {
			System.err.println("PersistDiagramSettings::parseSeriesEnabled: failed");
		}

		for (Entry<String, Boolean> e : list.contents()) {
			String name = e.getKey();
			boolean enabled = e.getValue();

			SimulationSerie s = getCollectionByName(name);
			if (s == null) {
				System.err.println("PersistDiagramSettings::SimulationSerie «" + name + "» not found!");
				continue;
			}
			renderer.setSeriesVisible(s.getChartId(), enabled);
		}
	}

	private String getEnabledSeriesString() {
		PersistListBoolean list = new PersistListBoolean("visibility");
		for (SimulationSerie s : this.collection) {
			list.set(s.getName(), renderer.isSeriesVisible(s.getChartId()));
		}

		return list.asString();
	}

	// ///////////////////////////////////////////////
	// / Stroke
	// ///////////////////////////////////////////////
	private String getSeriesStroke() {
		PersistListStroke list = new PersistListStroke("stroke");
		for (SimulationSerie s : this.collection) {
			list.set(s.getName(), (BasicStroke) renderer.lookupSeriesStroke(s.getChartId()));
		}

		return list.asString();
	}

	private void parseSeriesStroke(String data) {
		PersistListStroke list = new PersistListStroke("stroke");
		if (!list.parse(data)) {
			System.err.println("PersistDiagramSettings::parseSeriesStroke: failed");
		}

		for (Entry<String, BasicStroke> e : list.contents()) {
			String name = e.getKey();
			BasicStroke stroke = e.getValue();

			SimulationSerie s = getCollectionByName(name);
			if (s == null) {
				System.err.println("PersistDiagramSettings::SimulationSerie «" + name + "» not found!");
				continue;
			}
			renderer.setSeriesStroke(s.getChartId(), stroke);
		}
	}

	// ///////////////////////////////////////////////
	// / Color
	// ///////////////////////////////////////////////
	private String getSeriesColor() {
		PersistListColor list = new PersistListColor("color");
		for (SimulationSerie s : this.collection) {
			list.set(s.getName(), (Color) renderer.lookupSeriesPaint(s.getChartId()));
		}

		return list.asString();
	}

	private void parseSeriesColor(String data) {
		PersistListColor list = new PersistListColor("color");
		if (!list.parse(data)) {
			System.err.println("PersistDiagramSettings::parseSeriesColor: failed");
		}

		for (Entry<String, Color> e : list.contents()) {
			String name = e.getKey();
			Color color = e.getValue();

			SimulationSerie s = getCollectionByName(name);
			if (s == null) {
				System.err.println("PersistDiagramSettings::SimulationSerie «" + name + "» not found!");
				continue;
			}
			renderer.setSeriesPaint(s.getChartId(), color);
		}
	}

	// ///////////////////////////////////////////////
	// / Public API
	// ///////////////////////////////////////////////
	public void save(SimulationConfiguration simConfig) {
		simConfig.setParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, this.getEnabledSeriesString());
		simConfig.setParameter(StandardParameter.DIAGRAM_SERIES_COLOR, this.getSeriesColor());
		simConfig.setParameter(StandardParameter.DIAGRAM_SERIES_STROKE, this.getSeriesStroke());
	}

	public void load(SimulationConfiguration simConfig) {
		this.parseSeriesEnabled(simConfig.getParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, null));
		this.parseSeriesColor(simConfig.getParameter(StandardParameter.DIAGRAM_SERIES_COLOR, null));
		this.parseSeriesStroke(simConfig.getParameter(StandardParameter.DIAGRAM_SERIES_STROKE, null));
	}

}
