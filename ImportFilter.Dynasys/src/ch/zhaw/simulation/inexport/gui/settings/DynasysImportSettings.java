package ch.zhaw.simulation.inexport.gui.settings;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import butti.javalibs.config.Settings;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.dialog.settings.SettingsPanel;
import ch.zhaw.simulation.inexport.dynasys.DynasysModel;

/**
 * This is an additional Settings page for the Dynasys import
 * 
 * @author Andreas Butti
 */
public class DynasysImportSettings extends JPanel implements SettingsPanel {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;

	private JSpinner txtScale = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 5.0, 0.1));

	private JCheckBox cbAlign = new JCheckBox("Dokument automatisch neu ausrichten");

	private JSpinner paddingTop = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
	private JSpinner paddingLeft = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));

	private JSpinner flowpointMove = new JSpinner(new SpinnerNumberModel(10, -50, 50, 1));

	private Component lbPaddingTop;
	private Component lbPaddingLeft;

	private DynasysModel model;

	public DynasysImportSettings(Settings settings) {
		gbm = new GridBagManager(this);
		model = new DynasysModel(settings);

		gbm.setX(0).setY(0).setWeightY(0).setComp(new JLabel("Skalierungsfaktor"));
		gbm.setX(1).setY(0).setWeightY(0).setComp(txtScale);

		gbm.setX(0)
				.setWidth(2)
				.setY(1)
				.setWeightY(0)
				.setComp(
						getInfolabel("<html>Dynasys Symbole sind viel kleiner als die von (AB)²,<br>daher kann das ganze Dokument skaliert werden,<br>um es ordnungsgemäss darzustellen.</html>"));

		gbm.setX(0).setY(3).setWeightY(0).setComp(new JLabel("Flusssymbol verschieben (Y)"));
		gbm.setX(1).setY(3).setWeightY(0).setComp(flowpointMove);

		cbAlign.setFont(cbAlign.getFont().deriveFont(Font.BOLD));
		gbm.setX(0).setWidth(2).setY(10).setWeightY(0).setComp(cbAlign);
		lbPaddingTop = gbm.setX(0).setY(11).setWeightY(0).setComp(new JLabel("Abstand oben"));
		gbm.setX(1).setY(11).setWeightY(0).setComp(paddingTop);

		lbPaddingLeft = gbm.setX(0).setY(12).setWeightY(0).setComp(new JLabel("Abstand links"));
		gbm.setX(1).setY(12).setWeightY(0).setComp(paddingLeft);

		JButton reset = new JButton("Zurücksetzten");
		gbm.setX(0).setWidth(2).setY(20).setWeightY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.LINE_END).setComp(reset);
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.reset();
				readDataFromModel();
			}
		});

		initData();
		readDataFromModel();
	}

	private void initData() {
		cbAlign.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				realignStateChanged();
			}
		});
	}

	private void readDataFromModel() {
		txtScale.setValue(model.getScaleFactor());
		cbAlign.setSelected(model.isRealign());
		paddingTop.setValue(model.getPaddingTop());
		paddingLeft.setValue(model.getPaddingLeft());
		flowpointMove.setValue(model.getFlowPointMove());

		realignStateChanged();
	}

	protected void realignStateChanged() {
		boolean b = cbAlign.isSelected();
		model.setRealign(b);
		lbPaddingLeft.setEnabled(b);
		lbPaddingTop.setEnabled(b);
		paddingLeft.setEnabled(b);
		paddingTop.setEnabled(b);
	}

	private JLabel getInfolabel(String text) {
		JLabel lb = new JLabel(text);
		lb.setFont(lb.getFont().deriveFont(Font.ITALIC));
		return lb;
	}

	@Override
	public JPanel getContentsPanel() {
		return this;
	}

	@Override
	public void saveSettings() {
		model.setScaleFactor((Double) txtScale.getValue());
		model.setPaddingLeft((Integer) paddingLeft.getValue());
		model.setPaddingTop((Integer) paddingTop.getValue());
		model.setFlowPointMove((Integer) flowpointMove.getValue());
	}
}
