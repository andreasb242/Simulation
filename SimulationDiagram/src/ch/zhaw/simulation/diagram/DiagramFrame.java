package ch.zhaw.simulation.diagram;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.action.TargetableAction;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import butti.fontchooser.EditorDialog;
import butti.javalibs.config.Settings;
import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.diagram.LogButton.Direction;
import ch.zhaw.simulation.diagram.charteditor.SimulationChartEditor;
import ch.zhaw.simulation.diagram.csv.CSVSaver;
import ch.zhaw.simulation.diagram.export.ChartExportHelper;
import ch.zhaw.simulation.diagram.persist.DiagramConfiguration;
import ch.zhaw.simulation.diagram.persist.PersistDiagramSettings;
import ch.zhaw.simulation.diagram.sidebar.DiagramSidebar;
import ch.zhaw.simulation.diagram.tableview.TableDialog;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.simplecharteditor.SimpleChartEditor;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;

public class DiagramFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private DiagramSidebar sidebar;

	private Toolbar toolbar;

	private ChartPanel chartPanel;

	private XYPlot plot;

	private TableDialog tableDialog;

	private Settings settings;

	private SimulationCollection collection;

	private LogButton buttonLogX;

	private LogButton buttonLogY;

	private JComboBox cbY;

	private PersistDiagramSettings persitSettings;

	private DiagramConfiguration config;

	private JFreeChart chart;

	public DiagramFrame(SimulationCollection collection, final Settings settings, DiagramConfiguration config, final String name, final Sysintegration sys) {
		this.settings = settings;
		this.collection = collection;
		this.config = config;

		toolbar = sys.createToolbar(32);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(name + " - (AB)² Simulation");
		setLayout(new BorderLayout());

		cbY = new JComboBox();
		cbY.addItem("Zeit");
		for (SimulationSerie s : this.collection) {
			cbY.addItem(s);
		}

		cbY.setRenderer(new SerieCbRenderer(sys));

		add(toolbar.getComponent(), BorderLayout.NORTH);

		ChartTheme currentTheme = new SimulationDiagramTheme("(AB)²");

		XYSeriesCollection col = new XYSeriesCollection();
		int id = 0;
		for (SimulationSerie s : collection) {
			XYSeries serie = new XYSeries(s.getName());
			for (SimulationEntry d : s.getData()) {
				serie.add(d.time, d.value);
			}
			col.addSeries(serie);

			s.setChartId(id);
			id++;
		}

		boolean tooltips = true;

		NumberAxis xAxis = new NumberAxis(null);
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(null);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		this.plot = new XYPlot(col, xAxis, yAxis, renderer);
		this.plot.setOrientation(PlotOrientation.VERTICAL);

		if (tooltips) {
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		}

		/**
		 * Always create legend, only hide / show if the user chooses
		 */
		this.chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
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
				if (settings.isSetting("extendedDiagramSettings", false)) {
					SimulationChartEditor editor = new SimulationChartEditor(chart);

					EditorDialog dlg = EditorDialog.create(this, localizationResources.getString("Chart_Properties"), editor);
					if (dlg.display()) {
						DiagramFrame.this.config.set("charteditor.style", "NOT_SET");
						editor.updateChart(this.getChart());
					}
				} else {
					SimpleChartEditor editor = new SimpleChartEditor(DiagramFrame.this, chart, DiagramFrame.this.config);
					editor.setVisible(true);
				}
			}
		};

		this.persitSettings = new PersistDiagramSettings(this.collection, renderer, chart);
		this.persitSettings.load(config);

		this.sidebar = new DiagramSidebar(this.collection, renderer, sys);
		add(sidebar, BorderLayout.WEST);

		add(chartPanel, BorderLayout.CENTER);

		initToolbar();

		this.toolbar.addSeparator();
		this.toolbar.add(new JLabel(" x-Achse: "));
		this.toolbar.add(cbY);

		setSize(640, 480);
		setLocationRelativeTo(null);// center

		// restore old position
		new WindowPositionSaver(this);
	}

	private void initToolbar() {
		toolbar.add(new ToolbarAction("Anzeigen als Tabelle", "diagram/spreadsheet") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tableDialog == null) {
					tableDialog = new TableDialog(DiagramFrame.this, settings, collection);
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

				if (range.getUpperBound() + offset > collection.getEndTime()) {
					offset = collection.getEndTime() - range.getUpperBound();
				}

				if (offset != 0) {
					axis.setRange(range.getLowerBound() + offset, range.getUpperBound() + offset);
				}
			}
		});

		toolbar.addSeparator();

		this.buttonLogX = new LogButton(this, plot, toolbar, Direction.X);
		this.buttonLogY = new LogButton(this, plot, toolbar, Direction.Y);

		toolbar.addSeparator();

		TargetableAction legend = new TargetableAction("Legende", IconLoader.getIcon("diagram/legend", toolbar.getDefaultIconSize())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemStateChanged(ItemEvent evt) {
				boolean showLegend = evt.getStateChange() == ItemEvent.SELECTED;
				chart.getLegend().setVisible(showLegend);
			}
		};

		legend.setStateAction(true);
		toolbar.addToogleAction(legend);

		if (chart.getLegend().isVisible()) {
			legend.setSelected(true);
		}

		final JButton btLegende = new JButton("Lengende");
		toolbar.add(btLegende);

		btLegende.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JPopupMenu p = new JPopupMenu();

				JMenuItem it = new JMenuItem("Links");
				it.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						LegendTitle l = DiagramFrame.this.chart.getLegend();
						l.setPosition(RectangleEdge.LEFT);
					}
				});
				p.add(it);
				it = new JMenuItem("Rechts");
				it.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						LegendTitle l = DiagramFrame.this.chart.getLegend();
						l.setPosition(RectangleEdge.RIGHT);
					}
				});
				p.add(it);
				it = new JMenuItem("Oben");
				it.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						LegendTitle l = DiagramFrame.this.chart.getLegend();
						l.setPosition(RectangleEdge.TOP);
					}
				});
				p.add(it);
				it = new JMenuItem("Unten");
				it.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						LegendTitle l = DiagramFrame.this.chart.getLegend();
						l.setPosition(RectangleEdge.BOTTOM);
					}
				});
				p.add(it);

				p.show(btLegende, 0, btLegende.getHeight());
			}
		});

	}

	@Override
	public void dispose() {
		sidebar.dispose();
		if (tableDialog != null) {
			tableDialog.dispose();
		}
		this.buttonLogX.dispose();
		this.buttonLogY.dispose();

		this.persitSettings.save(config);

		super.dispose();
	}
}
