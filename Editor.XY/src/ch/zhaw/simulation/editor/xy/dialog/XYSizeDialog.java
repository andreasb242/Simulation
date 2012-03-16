package ch.zhaw.simulation.editor.xy.dialog;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXLabel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.ButtonFactory;
import butti.javalibs.gui.GridBagManager;
import butti.javalibs.numerictextfield.NumericTextField;

public class XYSizeDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;
	
	private NumericTextField txtWidth = new NumericTextField();
	private NumericTextField txtHeight = new NumericTextField();
	

	public XYSizeDialog(JFrame parent) {
		super(parent);

		gbm = new GridBagManager(this, true);

		gbm.setX(0).setY(0).setWidth(4).setWeightY(0).setComp(new TitleLabel("Simulationsfläche"));
		JXLabel lb = new JXLabel("Die Grösse der Simulation, Innerhalb dieser Fläche werden die Meso Kompartmente platziert. "
				+ "Diese können sich auch wärend der Simulation nicht ausserhalb dieser definierten Fläche bewegen");
		lb.setLineWrap(true);
		gbm.setX(0).setY(1).setWidth(4).setWeightY(0).setComp(lb);

		gbm.setX(0).setY(10).setComp(new JLabel("Breite"));
		gbm.setX(0).setY(11).setComp(new JLabel("Höhe"));

		gbm.setX(1).setWidth(3).setY(10).setComp(txtWidth);
		gbm.setX(1).setWidth(3).setY(11).setComp(txtHeight);
		
		
		JButton btCancel = ButtonFactory.createButton("Abbrechen", false);
		JButton btOk = ButtonFactory.createButton("OK", true);
		
		gbm.setX(3).setY(100).setWeightX(0).setWeightY(0).setAnchor(GridBagConstraints.LINE_END).setComp(btCancel);
		gbm.setX(4).setY(100).setWeightX(0).setWeightY(0).setComp(btOk);

		
		pack();
		setLocationRelativeTo(parent);
	}

}
