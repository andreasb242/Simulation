package ch.zhaw.simulation.plugin.sidebar;

import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

/**
 * @author: bachi
 */
public class AdaptiveStepConfigurationPane extends DefaultConfigurationPane implements FocusListener {

	private SimulationConfiguration config;

	private JLabel lblInitStep = new JLabel("Initial Schritt");
	private NumericTextField ntInitStep  = new NumericTextField();
	private JLabel           lblMaxStep  = new JLabel("Maximaler Schritt");
	private NumericTextField ntMaxStep   = new NumericTextField();

	public AdaptiveStepConfigurationPane(DefaultConfigurationSidebar sidebar) {
		super(sidebar);
	}

	private void load() {
		ntInitStep.setValue(this.config.getParameter(StandardParameter.INIT_STEP, 0));
		ntMaxStep.setValue(this.config.getParameter(StandardParameter.MAX_STEP, 0));
	}

	@Override
	public void loadDataFromModel() {
		super.loadDataFromModel();
	}

	@Override
	public void add() {
		super.add();
		sidebar.add(lblInitStep);
		sidebar.add(ntInitStep);
		sidebar.add(lblMaxStep);
		sidebar.add(ntMaxStep);
	}

	@Override
	public void remove() {
		sidebar.remove(lblInitStep);
		sidebar.remove(ntInitStep);
		sidebar.remove(lblMaxStep);
		sidebar.remove(ntMaxStep);
		super.remove();
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		try {
			this.config.setParameter(StandardParameter.INIT_STEP, ntInitStep.getDoubleValue());
		} catch (ParseException ex) {
			ntInitStep.setValue(this.config.getParameter(StandardParameter.INIT_STEP, 0));
		}
		try {
			this.config.setParameter(StandardParameter.MAX_STEP, ntMaxStep.getDoubleValue());
		} catch (ParseException ex) {
			ntMaxStep.setValue(this.config.getParameter(StandardParameter.MAX_STEP, 0));
		}
	}

	@Override
	public void propertyChanged(String property, String newValue) {
		super.propertyChanged(property, newValue);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		super.propertyChanged(property, newValue);    //To change body of overridden methods use File | Settings | File Templates.
	}
}
