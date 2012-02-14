package simulation.gui;

import gui.control.SimulationControl;
import gui.diagramm.Diagramm;
import gui.diagramm.DiagrammControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import ch.zhaw.simulation.sysintegration.Toolbar;
import ch.zhaw.simulation.sysintegration.Toolbar.ToolbarAction;

import butti.javalibs.config.WindowPositionSaver;
import butti.javalibs.config.WindowPositionSaver.AdditionalWindowPositions;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;

import simulation.data.SimulationCollection;
import controls.zoom.DiagrammZoom;



public class SimulationDiagrammDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private DiagrammSettings settings;
	private Diagramm diagramm;

	private JScrollPane scroll;
	private DiagrammControl diagrammControl;

	private GridBagManager gbm;

	private SimulationControl control;

	private Toolbar toolbar;

	private TableDialog tableDialog;

	private SimulationCollection series;

	private JCheckBox automateScale = new JCheckBox("Automatisch skalieren");

	public SimulationDiagrammDialog(SimulationControl control, SimulationCollection series) {
		super(control.getParent());
		this.control = control;
		this.series = series;
		gbm = new GridBagManager(this);

		diagrammControl = new DiagrammControl(series);

		String title = control.getDocumentName();

		if (title == null) {
			title = "Zeitablauf";
		} else {
			title = title + " - Zeitablauf";
		}

		setTitle(title);

		settings = new DiagrammSettings(diagrammControl);
		diagramm = new Diagramm(diagrammControl);

		scroll = new JScrollPane(diagramm, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		gbm.setX(0).setY(10).setComp(scroll);
		gbm.setX(1).setWeightX(0).setY(10).setComp(settings);

		pack();
		setSize(getSize().width + 50, getSize().height + 50);
		setLocationRelativeTo(control.getParent());
		new WindowPositionSaver(this, new AdditionalWindowPositions() {

			@Override
			public void load(Properties p) {
				if ("true".equals(p.get("autoscale"))) {
					automateScale.setSelected(true);
					diagrammControl.setAutoScale(true);
				}
			}

			@Override
			public void save(Properties p) {
				p.setProperty("autoscale", automateScale.isSelected() ? "true" : "false");
			}

		});

		initToolbar();

		automateScale.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				diagrammControl.setAutoScale(automateScale.isSelected());
			}
		});
	}

	private void initToolbar() {
		toolbar = control.getSysintegration().createToolbar();
		gbm.setX(0).setY(0).setWidth(2).setWeightY(0).setComp(toolbar.getComponent());

		DiagrammZoom horizontalZoom = new DiagrammZoom();
		DiagrammZoom verticalZoom = new DiagrammZoom();

		toolbar.add(new ToolbarAction("In Tabelle anzeigen", "table") {

			@Override
			protected void actionPerformed(ActionEvent e) {
				if (tableDialog == null) {
					tableDialog = new TableDialog(control, series);
				} else {
					tableDialog.setData(series);
				}

				tableDialog.pack();
				tableDialog.setLocationRelativeTo(SimulationDiagrammDialog.this);
				tableDialog.setVisible(true);
			}
		});

		toolbar.addSeparator();

		toolbar.add(new JLabel(" X: "));
		toolbar.add(horizontalZoom);
		toolbar.addSeparator();

		toolbar.add(new JLabel(" Y: "));
		toolbar.add(verticalZoom);

		toolbar.addSeparator();
		toolbar.add(automateScale);
		// TODO: export als Bild (Kamera Symbol)

	}
}
