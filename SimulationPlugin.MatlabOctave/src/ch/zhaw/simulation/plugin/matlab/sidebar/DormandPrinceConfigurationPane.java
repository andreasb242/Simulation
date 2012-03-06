package ch.zhaw.simulation.plugin.matlab.sidebar;

import butti.javalibs.gui.GridBagManager;
import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.matlab.MatlabParameter;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

/**
 * @author: bachi
 */
public class DormandPrinceConfigurationPane extends JPanel implements FocusListener {

	private SimulationConfiguration config;

	private NumericTextField ntStart = new NumericTextField();
	private NumericTextField ntEnd = new NumericTextField();
	private NumericTextField ntInitStep = new NumericTextField();
	private NumericTextField ntMaxStep = new NumericTextField();

	public DormandPrinceConfigurationPane(SimulationConfiguration config) {
		GridBagManager gbm = new GridBagManager(this);

		this.config = config;

		gbm.setX(0).setY(0).setWeightY(0).setWeightX(0).setComp(new JLabel("Start"));
		gbm.setX(1).setY(0).setWeightY(0).setComp(ntStart);

		gbm.setX(0).setY(1).setWeightY(0).setWeightX(0).setComp(new JLabel("End"));
		gbm.setX(1).setY(1).setWeightY(0).setComp(ntEnd);

		gbm.setX(0).setY(2).setWeightY(0).setWeightX(0).setComp(new JLabel("Initial Schritt"));
		gbm.setX(1).setY(2).setWeightY(0).setComp(ntInitStep);

		gbm.setX(0).setY(3).setWeightY(0).setWeightX(0).setComp(new JLabel("Maximal Schritt"));
		gbm.setX(1).setY(3).setWeightY(0).setComp(ntMaxStep);
		
		load();
	}
	
	private void load() {
		ntInitStep.setValue(this.config.getParameter(MatlabParameter.INIT_STEP, 0));
		ntMaxStep.setValue(this.config.getParameter(MatlabParameter.MAX_STEP, 0));
		ntEnd.setValue(this.config.getParameter(MatlabParameter.END, 0));
		ntStart.setValue(this.config.getParameter(MatlabParameter.START, 0));
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		try {
			this.config.setParameter(MatlabParameter.INIT_STEP, ntInitStep.getDoubleValue());
		} catch (ParseException ex) {
			ntInitStep.setValue(this.config.getParameter(MatlabParameter.INIT_STEP, 0));
		}
		try {
			this.config.setParameter(MatlabParameter.MAX_STEP, ntMaxStep.getDoubleValue());
		} catch (ParseException ex) {
			ntMaxStep.setValue(this.config.getParameter(MatlabParameter.MAX_STEP, 0));
		}
		try {
			this.config.setParameter(MatlabParameter.END, ntEnd.getDoubleValue());
		} catch (ParseException ex) {
			ntEnd.setValue(this.config.getParameter(MatlabParameter.END, 0));
		}
		try {
			this.config.setParameter(MatlabParameter.START, ntStart.getDoubleValue());
		} catch (ParseException ex) {
			ntStart.setValue(this.config.getParameter(MatlabParameter.START, 0));
		}
	}
}
