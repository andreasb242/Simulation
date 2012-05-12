package ch.zhaw.simulation.diagram;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import org.jdesktop.swingx.action.TargetableAction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import butti.javalibs.config.WindowPositionSaver;
import ch.zhaw.simulation.diagram.plot.ZoomAndPositionHandler;
import ch.zhaw.simulation.diagram.sidebar.DiagramSidebar;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.Toolbar;

public class DiagramFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private DiagramConfigModel model;

	private DiagramSidebar sidebar;

//	private DiagramPlot plot;

	private Toolbar toolbar;

	private DiagramConfigListener listener;

	private SimulationConfiguration simConfig;

	private ZoomAndPositionHandler zoom = new ZoomAndPositionHandler();
	
	private DiagramConfiguration config = new DiagramConfiguration();

	public DiagramFrame(SimulationCollection collection, SimulationConfiguration simConfig, String name, Sysintegration sys) {
		this.model = new DiagramConfigModel(collection);

		System.out.println("load: " + simConfig.getParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, null));
		this.model.enableSeries(simConfig.getParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, null));
		this.sidebar = new DiagramSidebar(this.model);
//		this.plot = new DiagramPlot(this.model, zoom, this.config);
		
		this.simConfig = simConfig;
		toolbar = sys.createToolbar();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(name + " - (AB)² Simulation");
		setLayout(new BorderLayout());

		add(BorderLayout.NORTH, toolbar.getComponent());
		add(BorderLayout.WEST, sidebar);
		
		
		
		
		
		
		
		
		

// create a dataset...
XYSeries series1 = new XYSeries("Planned");
series1.add(1.0, 1.0);
series1.add(2.0, 4.0);
series1.add(3.0, -3.0);
series1.add(4.0, 5.0);
series1.add(5.0, 5.0);
series1.add(6.0, 7.0);
series1.add(7.0, 7.0);
series1.add(8.0, 8.0);

XYSeries series2 = new XYSeries("Delivered");
series2.add(1.0, 5.0);
series2.add(2.0, 7.0);
series2.add(3.0, 6.0);
series2.add(4.0, 8.0);
series2.add(5.0, -4.0);
series2.add(6.0, 4.0);
series2.add(7.0, 2.0);
series2.add(8.0, 1.0);

XYSeries series3 = new XYSeries("Third");
series3.add(3.0, 4.0);
series3.add(4.0, 3.0);
series3.add(5.0, 2.0);
series3.add(6.0, -3.0);
series3.add(7.0, 6.0);
series3.add(8.0, 3.0);
series3.add(9.0, -4.0);
series3.add(10.0, 3.0);

XYSeriesCollection dataset = new XYSeriesCollection();
dataset.addSeries(series1);
dataset.addSeries(series2);
dataset.addSeries(series3);

JFreeChart chart = ChartFactory.createXYLineChart("test", "x", "y", dataset, PlotOrientation.HORIZONTAL, true, true, false);

XYPlot plot = (XYPlot)chart.getPlot();

ChartPanel chartPanel = new ChartPanel(chart);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
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

		{
			toolbar.add(new JLabel("ZoomX"));

			final JSpinner zoomx = new JSpinner(new SpinnerNumberModel(100, 50, 10000000, 10));

			JComponent comp = zoomx.getEditor();
			JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
			DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
			formatter.setCommitsOnValidEdit(true);
			zoomx.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					zoom.setZoomX(((Integer) zoomx.getValue()));
				}
			});
			toolbar.add(zoomx);

		}

		{
			toolbar.add(new JLabel("ZoomY"));

			final JSpinner zoomy = new JSpinner(new SpinnerNumberModel(100, 50, 10000000, 10));

			JComponent comp = zoomy.getEditor();
			JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
			DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
			formatter.setCommitsOnValidEdit(true);
			zoomy.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					zoom.setZoomY(((Integer) zoomy.getValue()));
				}
			});

			toolbar.add(zoomy);
		}

		{
			toolbar.add(new JLabel("OffsetX"));

			final JSpinner offsetX = new JSpinner(new SpinnerNumberModel(0, -400, 400, 10));

			JComponent comp = offsetX.getEditor();
			JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
			DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
			formatter.setCommitsOnValidEdit(true);
			offsetX.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					zoom.setOffsetX(((Integer) offsetX.getValue()));
				}
			});

			toolbar.add(offsetX);
		}

		{
			toolbar.add(new JLabel("OffsetY"));

			final JSpinner offsetY = new JSpinner(new SpinnerNumberModel(100, -400, 400, 10));

			JComponent comp = offsetY.getEditor();
			JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
			DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
			formatter.setCommitsOnValidEdit(true);
			offsetY.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					zoom.setOffsetY((int) ((Integer) offsetY.getValue()));
				}
			});

			toolbar.add(offsetY);
		}
	}

	@Override
	public void dispose() {
		// TODO !!! plot.dispose();
		sidebar.dispose();
		model.removeListener(listener);

		System.out.println("save: " + model.getEnabledSeriesString());
		simConfig.setParameter(StandardParameter.DIAGRAM_LAST_VIEWED_SERIES, model.getEnabledSeriesString());

		super.dispose();
	}
}
