package ch.zhaw.simulation.editor.xy.dialog;

import javax.swing.JFrame;

import org.jdesktop.swingx.JXLabel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.BDialog;
import butti.javalibs.gui.GridBagManager;

public class XYSizeDialog extends BDialog {
	private static final long serialVersionUID = 1L;

	private GridBagManager gbm;
	
	public XYSizeDialog(JFrame parent) {
		super(parent);
		
		gbm = new GridBagManager(this, true);
		
		gbm.setX(0).setY(0).setWidth(2).setWeightY(0).setComp(new TitleLabel("Simulationsfläche"));
		JXLabel lb = new JXLabel("Die Grösse der Simulation, ");
		lb.setLineWrap(true);
		gbm.setX(0).setY(1).setWidth(2).setWeightY(0).setComp(lb);
		
		gbm.setX(0).setY(0).setWeightY(0).setComp(new TitleLabel("Breite"));
		gbm.setX(0).setY(1).setWeightY(0).setComp(new TitleLabel("Höhe"));
		
		pack();
		setLocationRelativeTo(parent);
	}
	
	
	
}
