package ch.zhaw.simulation.diagram;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.action.TargetableAction;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.omg.CORBA.StringHolder;

import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.util.StringUtil;
import ch.zhaw.simulation.diagram.charteditor.SimulationChartEditorFactory;
import ch.zhaw.simulation.diagram.sidebar.DiagramSidebar;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationEntry;
import ch.zhaw.simulation.plugin.data.SimulationSerie;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;

public class DiagramFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private DiagramConfigModel model;

	private DiagramSidebar sidebar;

	private Toolbar toolbar;

	private DiagramConfigListener listener;

	private SimulationConfiguration simConfig;

	private ChartPanel chartPanel;

	private NumberAxis yAxis;
	private NumberAxis yAxisLog;

	private XYPlot plot;

	static {
		// init JFreeChart
		ChartEditorManager.setChartEditorFactory(new SimulationChartEditorFactory());
	}

	public DiagramFrame(SimulationCollection collection, SimulationConfiguration simConfig, String name, Sysintegration sys) {
		this.model = new DiagramConfigModel(collection);

		System.out.println("load: " + simConfig.getParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, null));
		this.model.enableSeries(simConfig.getParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, null));

		this.simConfig = simConfig;
		toolbar = sys.createToolbar();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(name + " - (AB)² Simulation");
		setLayout(new BorderLayout());

		add(BorderLayout.NORTH, toolbar.getComponent());

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
		this.yAxis = new NumberAxis(null);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		this.plot = new XYPlot(col, xAxis, yAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		if (tooltips) {
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		}

		JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		currentTheme.apply(chart);

		this.chartPanel = new ChartPanel(chart);

		this.sidebar = new DiagramSidebar(this.model, renderer);
		add(BorderLayout.WEST, sidebar);

		add(BorderLayout.CENTER, chartPanel);

		initToolbar();

		setSize(640, 480);
		setLocationRelativeTo(null);// center

		// restore old position
		new WindowPositionSaver(this);
	}

	private void initToolbar() {
		final int ICON_SIZE = 32;

		toolbar.add(new AbstractAction("Anzeigen als Tabelle", IconLoader.getIcon("diagram/spreadsheet", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("Speichern als CSV", IconLoader.getIcon("diagram/save", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.add(new AbstractAction("Speichern als Bild", IconLoader.getIcon("diagram/screenshot", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("Drucken", IconLoader.getIcon("diagram/print", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.PRINT_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("In die Zwischenablage kopieren (Rastergrafik)", IconLoader.getIcon("diagram/edit-copy", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.COPY_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("Diagramm Eigenschaften", IconLoader.getIcon("diagram/properties", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.PROPERTIES_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("Vergrössern", IconLoader.getIcon("diagram/zoom-in", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_IN_BOTH_COMMAND));
			}
		});

		toolbar.add(new AbstractAction("Verkleinern", IconLoader.getIcon("diagram/zoom-out", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_OUT_BOTH_COMMAND));
			}
		});

		toolbar.add(new AbstractAction("Passend", IconLoader.getIcon("diagram/zoom-original", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_RESET_BOTH_COMMAND));
			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("Vergrössern (x)", IconLoader.getIcon("diagram/zoom-in-x", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_IN_DOMAIN_COMMAND));
			}
		});

		toolbar.add(new AbstractAction("Verkleinern (x)", IconLoader.getIcon("diagram/zoom-out-x", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DiagramFrame.this.chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.ZOOM_OUT_DOMAIN_COMMAND));
			}
		});

		toolbar.add(new AbstractAction("Nach links", IconLoader.getIcon("diagram/left", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

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

		toolbar.add(new AbstractAction("Nach rechts", IconLoader.getIcon("diagram/right", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

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

		final TargetableAction action = new TargetableAction("Logarithmisch", "diagram/log", IconLoader.getIcon("diagram/log", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent evt) {
			}

			@Override
			public void itemStateChanged(ItemEvent evt) {
				boolean newValue;
				newValue = evt.getStateChange() == ItemEvent.SELECTED;

				model.setLogEnabled(newValue);
			}
		};
		action.setStateAction(true);

		listener = new DiagramConfigAdapter() {
			@Override
			public void setLogEnabled(boolean log) {
				// check if we can show a log axis, all values have to be > 0

				if (log && !canShowLogAxis()) {
					Messagebox.showInfo(DiagramFrame.this, "Logarithmische Achsen",
							"<html>Logarithmische achsen können nicht angewendet werden, da das Diagramm Negativ- oder Nullwerte enthält.<br>"
									+ "Blenden Sie nur rein positive Serien ein und versuchen Sie es erneut.</html>");

					model.setLogEnabled(false);
					return;
				}

				if (log) {
					if (DiagramFrame.this.yAxisLog == null) {
						DiagramFrame.this.yAxisLog = new LogarithmicAxis(null);
					}

					String oldLabel = DiagramFrame.this.yAxisLog.getLabel();
					String newLabel = DiagramFrame.this.yAxis.getLabel();

					if (!StringUtil.equals(oldLabel, newLabel)) {
						DiagramFrame.this.yAxisLog.setLabel(newLabel);
					}

					DiagramFrame.this.plot.setRangeAxis(DiagramFrame.this.yAxisLog);
				} else {
					if (DiagramFrame.this.yAxisLog == null) {
						return;
					}

					String oldLabel = DiagramFrame.this.yAxis.getLabel();
					String newLabel = DiagramFrame.this.yAxisLog.getLabel();

					if (!StringUtil.equals(oldLabel, newLabel)) {
						DiagramFrame.this.yAxis.setLabel(newLabel);
					}

					DiagramFrame.this.plot.setRangeAxis(DiagramFrame.this.yAxis);
				}

				if (action.isSelected() != log) {
					action.setSelected(log);
				}
			}
		};
		this.model.addListener(listener);

		toolbar.addToogleAction(action);
	}

	protected boolean canShowLogAxis() {
		for (SimulationSerie s : model.getCollection()) {
			if (model.isEnabled(s)) {
				for (SimulationEntry d : s.getData()) {
					if (d.value <= 0) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public void dispose() {
		sidebar.dispose();
		model.removeListener(listener);
		simConfig.setParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, model.getEnabledSeriesString());

		super.dispose();
	}
}
