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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import butti.javalibs.config.WindowPositionSaver;
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
		NumberAxis yAxis = new NumberAxis(null);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		XYPlot plot = new XYPlot(col, xAxis, yAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		if (tooltips) {
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		}

		JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		currentTheme.apply(chart);

		ChartPanel chartPanel = new ChartPanel(chart);

		this.sidebar = new DiagramSidebar(this.model,  renderer);
		add(BorderLayout.WEST, sidebar);

		
		
		add(BorderLayout.CENTER, new JScrollPane(chartPanel));

		initToolbar();

		setSize(640, 480);
		setLocationRelativeTo(null);// center

		// restore old position
		new WindowPositionSaver(this);
	}

	private void initToolbar() {
		final int ICON_SIZE = 32;

		toolbar.add(new AbstractAction("Anzeigen als Tabelle", IconLoader.getIcon("spreadsheet", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("Speichern als CSV", IconLoader.getIcon("save", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.add(new AbstractAction("Speichern als Bild", IconLoader.getIcon("photos", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.addSeparator();

		toolbar.add(new AbstractAction("Vergrössern", IconLoader.getIcon("zoom-in", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.add(new AbstractAction("Verkleinern", IconLoader.getIcon("zoom-out", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.add(new AbstractAction("Passend", IconLoader.getIcon("zoom-fit-best", ICON_SIZE)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		toolbar.addSeparator();

		final TargetableAction action = new TargetableAction("Logarithmisch", "log", IconLoader.getIcon("log", ICON_SIZE)) {
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
				if (action.isEnabled() != log) {
					action.setEnabled(log);
				}
			}
		};

		toolbar.addToogleAction(action);
	}

	@Override
	public void dispose() {
		sidebar.dispose();
		model.removeListener(listener);

		System.out.println("save: " + model.getEnabledSeriesString());
		simConfig.setParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, model.getEnabledSeriesString());

		super.dispose();
	}
}
