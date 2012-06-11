package ch.zhaw.simulation.editor.xy.dialog;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;
import butti.javalibs.gui.messagebox.Messagebox;
import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.editor.xy.XYDefaultSettingsHandler;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class XYSizeDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private NumericTextField txtWidth = new NumericTextField();
	private NumericTextField txtHeight = new NumericTextField();

	private NumericTextField txtX = new NumericTextField();
	private NumericTextField txtY = new NumericTextField();

	private JCheckBox cbShowGrid = new JCheckBox("Raster anzeigen");
	private SpinnerModel spModel = new SpinnerNumberModel(20, 10, 200, 1);
	private JSpinner spGrid = new JSpinner(spModel);

	private JRadioButton rColor = new JRadioButton("Farben");
	private JRadioButton rArrows = new JRadioButton("Pfeilen (Gradienten)");
	private JRadioButton rBoth = new JRadioButton("Farben und Pfeilen (Gradienten)");

	private SimulationXYModel model;

	private XYDefaultSettingsHandler xyDefaultSettingsHandler;

	public XYSizeDialog(final JFrame parent, SimulationXYModel model, XYDefaultSettingsHandler xyDefaultSettingsHandler) {
		super(parent);
		this.model = model;
		this.xyDefaultSettingsHandler = xyDefaultSettingsHandler;

		setTitle("Modell Konfiguration");

		gbm = new GridBagManager(this);

		gbm.setX(0).setY(0).setWidth(5).setWeightY(0).setComp(new TitleLabel("Simulationsfläche"));
		JLabel lb = new JLabel("<html>Die Grösse der Simulation, Innerhalb dieser<br>Fläche werden die Meso Kompartmente platziert.<br>"
				+ "Diese können sich auch wärend der Simulation nicht<br>ausserhalb dieser definierten Fläche bewegen</html>");
		gbm.setX(0).setY(1).setWidth(5).setWeightY(0).setComp(lb);

		gbm.setX(0).setY(10).setComp(new JLabel("Breite"));
		gbm.setX(0).setY(11).setComp(new JLabel("Höhe"));

		gbm.setX(1).setWidth(4).setY(10).setComp(txtWidth);
		gbm.setX(1).setWidth(4).setY(11).setComp(txtHeight);

		gbm.setX(0).setY(21).setComp(new JLabel("Nullpunkt"));
		gbm.setX(1).setY(21).setComp(txtX);
		gbm.setX(2).setY(21).setWeightX(0).setComp(new JLabel("/"));
		gbm.setX(3).setY(21).setWidth(2).setComp(txtY);

		JButton btCenter = new JButton("Nullpunkt zentrieren");
		gbm.setX(0).setY(23).setWidth(5).setWeightY(0).setComp(btCenter);

		btCenter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Long tmp = txtWidth.getLongValue();
					if (tmp == null) {
						return;
					}

					long width = tmp;
					tmp = txtHeight.getLongValue();
					if (tmp == null) {
						return;
					}

					long height = tmp;

					txtX.setValue(width / 2);
					txtY.setValue(height / 2);

				} catch (ParseException e1) {
					Messagebox.showWarning(parent, "Ungültige Eingabe", "Bitte zuerst gültige Zahlen für Breite und Höhe eingeben");
				}
			}
		});

		gbm.setX(0).setY(50).setWidth(5).setWeightY(0).setComp(new TitleLabel("Raster"));
		gbm.setX(0).setY(51).setWidth(5).setComp(cbShowGrid);

		cbShowGrid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enableDisableGrid();

			}
		});

		gbm.setX(0).setY(52).setComp(new JLabel("Abstand in px."));
		gbm.setX(1).setY(52).setWidth(4).setComp(spGrid);

		gbm.setX(0).setY(80).setWidth(5).setWeightY(0).setComp(new TitleLabel("Dichte darstellen mit"));

		ButtonGroup group = new ButtonGroup();
		group.add(rColor);
		group.add(rArrows);
		group.add(rBoth);

		gbm.setX(0).setY(81).setWidth(5).setComp(rColor);
		gbm.setX(0).setY(82).setWidth(5).setComp(rArrows);
		gbm.setX(0).setY(83).setWidth(5).setComp(rBoth);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!saveData()) {
					setVisible(true);
				}
			}

		});

		loadData();

		pack();
		setLocationRelativeTo(parent);
	}

	protected void enableDisableGrid() {
		spGrid.setEnabled(cbShowGrid.isSelected());
	}

	private void loadData() {
		// sets the size
		txtWidth.setValue(model.getWidth());
		txtHeight.setValue(model.getHeight());

		// 0 point
		Point p = model.getZero();
		txtX.setValue(p.x);
		txtY.setValue(p.y);

		// grid
		cbShowGrid.setSelected(model.isShowGrid());
		spGrid.setValue(model.getGrid());

		if (model.isShowDensityArrow() && model.isShowDensityColor()) {
			rBoth.setSelected(true);
		} else if (model.isShowDensityArrow()) {
			rArrows.setSelected(true);
		} else {
			rColor.setSelected(true);
		}

		enableDisableGrid();
	}

	private boolean saveData() {
		try {
			model.setWidth((int) (long) txtWidth.getLongValue());
			model.setHeight((int) (long) txtHeight.getLongValue());

			Point p = new Point((int) (long) txtX.getLongValue(), (int) (long) txtY.getLongValue());
			model.setZero(p);

			model.setShowGrid(cbShowGrid.isSelected());

			model.setGrid((int) (Integer) spGrid.getValue());

			if (rBoth.isSelected()) {
				model.setShowDensityArrow(true);
				model.setShowDensityColor(true);
			} else if (rArrows.isSelected()) {
				model.setShowDensityArrow(true);
				model.setShowDensityColor(false);
			} else if (rColor.isSelected()) {
				model.setShowDensityArrow(false);
				model.setShowDensityColor(true);
			}

			model.fireSizeRasterChanged();

			xyDefaultSettingsHandler.save(model);

			return true;
		} catch (ParseException e) {
			e.printStackTrace();

			// Should not happen...
			Messagebox.showError((JFrame) getParent(), "Ungültige Eingabe", "Bitte korrigieren Sie Ihre Eingaben");

			return false;
		}
	}
}
