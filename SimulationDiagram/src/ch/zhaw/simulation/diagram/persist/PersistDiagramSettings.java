package ch.zhaw.simulation.diagram.persist;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Map.Entry;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;

import ch.zhaw.simulation.diagram.SimulationDiagramTheme;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

public class PersistDiagramSettings {
	public interface PersistKeys {
		/**
		 * Diagram settings
		 */
		public static String LAST_VIEWED_SERIES = "series.visible";
		public static String SERIES_COLOR = "series.color";
		public static String SERIES_STROKE = "series.stroke";
	}

	private SimulationCollection collection;
	private XYLineAndShapeRenderer renderer;
	private JFreeChart chart;

	public PersistDiagramSettings(SimulationCollection collection, XYLineAndShapeRenderer renderer, JFreeChart chart) {
		this.collection = collection;
		this.chart = chart;
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
	// / Other
	// ///////////////////////////////////////////////

	private void saveOther(DiagramConfiguration cfg) {
		XYPlot plot = chart.getXYPlot();
		saveAxis(cfg, "x", plot.getDomainAxis());
		saveAxis(cfg, "y", plot.getRangeAxis());

		// TODO Speichern was die X achse ist

		// Header
		TextTitle title = chart.getTitle();

		if (title == null) {
			cfg.set("title.visible", "false");
			cfg.set("title.text", "");
			cfg.set("title.font", "");
			cfg.set("title.color", "");
		} else {
			cfg.set("title.visible", title.isVisible());
			cfg.set("title.text", title.getText());
			cfg.set("title.font", title.getFont());
			cfg.set("title.color", (Color) title.getPaint());
		}

		// Diagram
		cfg.set("diagram.antialiasing", chart.getAntiAlias());
		cfg.set("diagram.backgroundcolor", (Color) chart.getBackgroundPaint());

		// Plot
		cfg.set("plot.outline.color", (Color) plot.getOutlinePaint());
		cfg.set("plot.outline.stroke", (BasicStroke) plot.getOutlineStroke());
		cfg.set("plot.backgroundcolor", (Color) plot.getBackgroundPaint());

		// not editable by user
		// cfg.set("plot.insets", plot.getInsets());

	}

	private void loadOther(DiagramConfiguration cfg) {
		XYPlot plot = chart.getXYPlot();
		loadAxis(cfg, "x", plot.getDomainAxis());
		loadAxis(cfg, "y", plot.getRangeAxis());

		// Header
		TextTitle title = new TextTitle();
		title.setText(cfg.get("title.text", ""));
		title.setFont(cfg.get("title.font", SimulationDiagramTheme.DEFAULT_TITLE_FONT));
		title.setPaint(cfg.get("title.color", Color.BLACK));
		title.setVisible(cfg.get("title.visible", false));
		chart.setTitle(title);

		// Diagram
		chart.setAntiAlias(cfg.get("diagram.antialiasing", true));
		chart.setBackgroundPaint(cfg.get("diagram.backgroundcolor", Color.WHITE));

		// Plot
		plot.setOutlinePaint(cfg.get("plot.outline.color", SimulationDiagramTheme.DEFAULT_OUTLINE_PAINT));
		plot.setOutlineStroke(cfg.get("plot.outline.stroke", SimulationDiagramTheme.DEFAULT_OUTLINE_STROKE));
		cfg.set("plot.backgroundcolor", (Color) plot.getBackgroundPaint());
	}

	private void loadAxis(DiagramConfiguration cfg, String direction, ValueAxis axis) {
		axis.setLabel(cfg.get("axis." + direction + ".text", ""));

		axis.setLabelFont(cfg.get("axis." + direction + ".font", SimulationDiagramTheme.DEFAULT_FONT_AXIS));
		axis.setLabelPaint(cfg.get("axis." + direction + ".color", SimulationDiagramTheme.DEFAULT_AXIS_LABEL_PAINT));

		axis.setTickLabelFont(cfg.get("axis." + direction + ".marker.font", SimulationDiagramTheme.DEFAULT_FONT_AXIS));
		axis.setTickLabelPaint(cfg.get("axis." + direction + ".marker.color", SimulationDiagramTheme.DEFAULT_TICK_LABEL_PAINT));
		axis.setTickMarksVisible(cfg.get("axis." + direction + ".marker.showmarker", true));
		axis.setTickLabelsVisible(cfg.get("axis." + direction + ".marker.showtext", true));

		axis.setAutoRange(cfg.get("axis." + direction + ".values.auto", true));

		Range range = axis.getRange();
		double upper =range.getUpperBound();
		double lower = range.getLowerBound();
		
		lower=cfg.get("axis." + direction + ".values.min", lower);
		upper=cfg.get("axis." + direction + ".values.max", upper);

		axis.setRange(new Range(lower, upper));
		
		// TODO !!!!!!
		cfg.get("axis." + direction + ".layout.border", "");
		cfg.get("axis." + direction + ".layout.bordercolor", "");
		cfg.get("axis." + direction + ".layout.backgroundcolor", "");
		cfg.get("axis." + direction + ".layout.orientation", "");
		cfg.get("axis." + direction + ".layout.logarithmic", axis instanceof LogarithmicAxis);

	}

	private void saveAxis(DiagramConfiguration cfg, String direction, ValueAxis axis) {
		cfg.set("axis." + direction + ".text", axis.getLabel());
		cfg.set("axis." + direction + ".font", axis.getLabelFont());
		cfg.set("axis." + direction + ".color", (Color) axis.getLabelPaint());

		cfg.set("axis." + direction + ".marker.font", axis.getTickLabelFont());
		cfg.set("axis." + direction + ".marker.color", (Color) axis.getTickLabelPaint());
		cfg.set("axis." + direction + ".marker.showmarker", axis.isTickMarksVisible());
		cfg.set("axis." + direction + ".marker.showtext", axis.isTickLabelsVisible());

		cfg.set("axis." + direction + ".values.auto", axis.isAutoRange());
		Range range = axis.getRange();
		cfg.set("axis." + direction + ".values.min", range.getLowerBound());
		cfg.set("axis." + direction + ".values.max", range.getUpperBound());

		// TODO !!!!!!
		// cfg.set("axis." + direction + ".layout.border", "");
		// cfg.set("axis." + direction + ".layout.bordercolor", "");
		// cfg.set("axis." + direction + ".layout.backgroundcolor", "");
		// cfg.set("axis." + direction + ".layout.orientation", "");
		// cfg.set("axis." + direction + ".layout.logarithmic", axis instanceof
		// LogarithmicAxis);

	}

	// ///////////////////////////////////////////////
	// / Public API
	// ///////////////////////////////////////////////
	public void save(DiagramConfiguration cfg) {
		cfg.set(PersistKeys.LAST_VIEWED_SERIES, this.getEnabledSeriesString());
		cfg.set(PersistKeys.SERIES_COLOR, this.getSeriesColor());
		cfg.set(PersistKeys.SERIES_STROKE, this.getSeriesStroke());
		saveOther(cfg);
	}

	public void load(DiagramConfiguration cfg) {
		this.parseSeriesEnabled(cfg.get(PersistKeys.LAST_VIEWED_SERIES));
		this.parseSeriesColor(cfg.get(PersistKeys.SERIES_COLOR));
		this.parseSeriesStroke(cfg.get(PersistKeys.SERIES_STROKE));
		loadOther(cfg);
	}
}
