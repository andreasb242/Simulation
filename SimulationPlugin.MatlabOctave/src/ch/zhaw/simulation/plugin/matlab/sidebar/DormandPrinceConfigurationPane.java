package ch.zhaw.simulation.plugin.matlab.sidebar;

import butti.javalibs.gui.GridBagManager;
import butti.javalibs.numerictextfield.NumericTextField;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author: bachi
 */
public class DormandPrinceConfigurationPane extends JPanel implements FocusListener {

	private NumericTextField ntStart = new NumericTextField();
	private NumericTextField ntEnd = new NumericTextField();
	private NumericTextField ntInitStep = new NumericTextField();
	private NumericTextField ntMaxStep = new NumericTextField();

	public DormandPrinceConfigurationPane() {		GridBagManager gbm = new GridBagManager(this);

		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Start"));
		gbm.setX(1).setY(0).setWeightY(0).setComp(ntStart);

		gbm.setX(0).setY(1).setWeightY(0).setWeightX(0).setComp(new JLabel("End"));
		gbm.setX(1).setY(1).setWeightY(0).setComp(ntEnd);

		gbm.setX(0).setY(2).setWeightY(0).setWeightX(0).setComp(new JLabel("Initial Schritt"));
		gbm.setX(1).setY(2).setWeightY(0).setComp(ntInitStep);

		gbm.setX(0).setY(3).setWeightY(0).setWeightX(0).setComp(new JLabel("Maximal Schritt"));
		gbm.setX(1).setY(3).setWeightY(0).setComp(ntMaxStep);
	}

	@Override
	public void focusGained(FocusEvent e) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void focusLost(FocusEvent e) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
