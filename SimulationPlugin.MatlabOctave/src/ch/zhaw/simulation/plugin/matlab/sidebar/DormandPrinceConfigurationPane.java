package ch.zhaw.simulation.plugin.matlab.sidebar;

import butti.javalibs.gui.GridBagManager;
import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.matlab.MatlabParameter;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationPane;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

/**
 * @author: bachi
 */
public class DormandPrinceConfigurationPane extends DefaultConfigurationPane implements FocusListener, MatlabParameter {

	private JLabel           lblInitStep = new JLabel("Initial Schritt");
	private NumericTextField ntInitStep  = new NumericTextField();
	private JLabel           lblMaxStep  = new JLabel("Maximaler Schritt");
	private NumericTextField ntMaxStep   = new NumericTextField();

	public DormandPrinceConfigurationPane(DefaultConfigurationSidebar sidebar) {
		super(sidebar);
	}

	@Override
	public void loadDataFromModel() {
		super.loadDataFromModel();
		ntInitStep.setValue(sidebar.getConfig().getParameter(MatlabParameter.INIT_STEP, 0));
		ntMaxStep.setValue(sidebar.getConfig().getParameter(MatlabParameter.MAX_STEP, 0));

	}

	@Override
	public void add() {
		super.add();
		sidebar.add(lblInitStep);
		sidebar.add(ntInitStep);
		ntInitStep.addFocusListener(this);

		sidebar.add(lblMaxStep);
		sidebar.add(ntMaxStep);
		ntMaxStep.addFocusListener(this);
	}

	@Override
	public void remove() {
		sidebar.remove(lblInitStep);
		sidebar.remove(ntInitStep);
		ntInitStep.removeFocusListener(this);

		sidebar.remove(lblMaxStep);
		sidebar.remove(ntMaxStep);
		ntMaxStep.removeFocusListener(this);

		super.remove();
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		super.focusLost(e);
		try {
			sidebar.getConfig().setParameter(MatlabParameter.INIT_STEP, ntInitStep.getDoubleValue());
		} catch (ParseException ex) {
			ntInitStep.setValue(sidebar.getConfig().getParameter(MatlabParameter.INIT_STEP, 0));
		}
		try {
			sidebar.getConfig().setParameter(MatlabParameter.MAX_STEP, ntMaxStep.getDoubleValue());
		} catch (ParseException ex) {
			ntMaxStep.setValue(sidebar.getConfig().getParameter(MatlabParameter.MAX_STEP, 0));
		}
	}

	@Override
	public void propertyChanged(String property, String newValue) {
		super.propertyChanged(property, newValue);
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		super.propertyChanged(property, newValue);
	}
}
