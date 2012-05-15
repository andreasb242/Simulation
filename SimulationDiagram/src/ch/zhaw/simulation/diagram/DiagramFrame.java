package ch.zhaw.simulation.diagram;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import butti.fontchooser.EditorDialog;
import butti.javalibs.config.Settings;
import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.diagram.charteditor.SimulationChartEditorFactory;
import ch.zhaw.simulation.diagram.csv.CSVSaver;
import ch.zhaw.simulation.diagram.export.ChartExportHelper;
import ch.zhaw.simulation.diagram.sidebar.DiagramSidebar;
import ch.zhaw.simulation.diagram.tableview.TableDialog;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;

public class DiagramFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private DiagramConfigModel model;

	private DiagramSidebar sidebar;

	private Toolbar toolbar;

	private SimulationConfiguration simConfig;

	private ChartPanel chartPanel;

	private XYPlot plot;

	private TableDialog tableDialog;

	private Settings settings;

	private SimulationCollection collection;

	private LogButton buttonLogX;

	private LogButton buttonLogY;

	static {
		// init JFreeChart
		ChartEditorManager.setChartEditorFactory(new SimulationChartEditorFactory());
	}

	public DiagramFrame(SimulationCollection collection, final Settings settings, SimulationConfiguration simConfig, final String name, final Sysintegration sys) {
		this.model = new DiagramConfigModel(collection);
		this.settings = settings;
		this.collection = collection;

		this.model.enableSeries(simConfig.getParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, null));
		this.model.parseSeriesConfigString(simConfig.getParameter(StandardParameter.DIAGRAM_SERIES_CONFIG, null));

		this.simConfig = simConfig;
		toolbar = sys.createToolbar(32);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(name + " - (AB)² Simulation");
		setLayout(new BorderLayout());

		add(toolbar.getComponent(), BorderLayout.NORTH);

		ChartTheme currentTheme = new SimulationDiagramTheme("(AB)²");

		XYSeriesCollection col = new XYSeriesCollection();
		for (SimulationSerie s : collection) {
			XYSeries serie = new XYSeries(s.getName());
			for (SimulationEntry d : s.getData()) {
				serie.add(d.time, d.value);
			}
			col.addSeries(serie);
		}

		boolean tooltips = true;

		NumberAxis xAxis = new NumberAxis(null);
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(null);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		this.plot = new XYPlot(col, xAxis, yAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		if (tooltips) {
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		}

		JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		currentTheme.apply(chart);

		this.chartPanel = new ChartPanel(chart) {
			private static final long serialVersionUID = 1L;

			@Override
			public void doSaveAs() throws IOException {
				ChartExportHelper h = new ChartExportHelper(DiagramFrame.this, settings, sys);
				h.exportChart(this, name, getWidth(), getHeight());
			}

			@Override
			public void doEditChartProperties() {
				ChartEditor editor = ChartEditorManager.getChartEditor(this.getChart());

				EditorDialog dlg = EditorDialog.create(this, localizationResources.getString("Chart_Properties"), (Component) editor);
				if (dlg.display()) {
					editor.updateChart(this.getChart());
				}

			}

		};

		this.sidebar = new DiagramSidebar(this.model, renderer);
		add(sidebar, BorderLayout.WEST);

		add(chartPanel, BorderLayout.CENTER);

		initToolbar(xAxis, yAxis);

		setSize(640, 480);
		setLocationRelativeTo(null);// center

		// restore old position
		new WindowPositionSaver(this);
	}

	private void initToolbar(NumberAxis xAxis, NumberAxis yAxis) {
		toolbar.add(new ToolbarAction("Anzeigen als Tabelle", "diagram/spreadsheet") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tableDialog == null) {
					tableDialog = new TableDialog(DiagramFrame.this, settings, model.getCollection());
				}
				tableDialog.setVisible(true);
			}
		});

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("Speichern als CSV", "diagram/save") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CSVSaver saver = new CSVSaver(DiagramFrame.this, settings, collection);
				saver.save();
			}
		});

		toolbar.add(new ToolbarAction("Speichern als Bild", "diagram/screenshot") {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DiagramFrame.this.chartPanel.doSaveAs();
				} catch (IOException e1) {
					Errorhandler.showError(e1);
				}
			}
		});

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("Drucken", "diagram/print") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.PRINT_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("In die Zwischenablage kopieren (Rastergrafik)", "diagram/edit-copy") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.COPY_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("Diagramm Eigenschaften", "diagram/properties") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.PROPERTIES_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("Vergrössern", "diagram/zoom-in") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_IN_BOTH_COMMAND));
			}
		});

		toolbar.add(new ToolbarAction("Verkleinern", "diagram/zoom-out") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_OUT_BOTH_COMMAND));
			}
		});

		toolbar.add(new ToolbarAction("Passend", "diagram/zoom-original") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_RESET_BOTH_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new ToolbarAction("Vergrössern (x)", "diagram/zoom-in-x") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_IN_DOMAIN_COMMAND));
			}
		});

		toolbar.add(new ToolbarAction("Verkleinern (x)", "diagram/zoom-out-x") {

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_OUT_DOMAIN_COMMAND));
			}
		});

		toolbar.add(new ToolbarAction("Nach links", "diagram/left") {

			@Override
			public void actionPerformed(ActionEvent e) {
				ValueAxis axis = DiagramFrame.this.chartPanel.getChart().getXYPlot().getDomainAxis();
				Range range = axis.getRange();

				double offset = 10;

				SimulationCollection collection = model.getCollection();

				if (range.getLowerBound() - offset < collection.getStartTime()) {
					offset = range.getLowerBound() - collection.getStartTime();
				}

				if (offset != 0) {
					axis.setRange(range.getLowerBound() - offset, range.getUpperBound() - offset);
				}
			}
		});

		toolbar.add(new ToolbarAction("Nach rechts", "diagram/right") {

			@Override
			public void actionPerformed(ActionEvent e) {
				ValueAxis axis = DiagramFrame.this.chartPanel.getChart().getXYPlot().getDomainAxis();
				Range range = axis.getRange();

				double offset = 10;

				SimulationCollection collection = model.getCollection();

				if (range.getUpperBound() + offset > collection.getEndTime()) {
					offset = collection.getEndTime() - range.getUpperBound();
				}

				if (offset != 0) {
					axis.setRange(range.getLowerBound() + offset, range.getUpperBound() + offset);
				}
			}
		});

		toolbar.addSeparator();

		this.buttonLogX = new LogButton(this, plot, toolbar, model, xAxis, DiagramConfigListener.Direction.X);
		this.buttonLogY = new LogButton(this, plot, toolbar, model, yAxis, DiagramConfigListener.Direction.Y);

	}

	@Override
	public void dispose() {
		sidebar.dispose();
		if (tableDialog != null) {
			tableDialog.dispose();
		}
		this.buttonLogX.dispose();
		this.buttonLogY.dispose();
		simConfig.setParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, model.getEnabledSeriesString());
		simConfig.setParameter(StandardParameter.DIAGRAM_SERIES_CONFIG, model.getSeriesConfigString());

		super.dispose();
	}
}
