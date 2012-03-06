package ch.zhaw.simulation.plugin.sidebar;

import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;
import ch.zhaw.simulation.plugin.StandardParameter;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

/**
 * @author: bachi
 */
public class DefaultConfigurationPane implements FocusListener, SimulationParameterListener {

	protected DefaultConfigurationSidebar sidebar;

	private JLabel           lblStart = new JLabel("Startzeit");
	private NumericTextField ntStart  = new NumericTextField();
	
	private JLabel           lblEnd   = new JLabel("Endzeit");
	private NumericTextField ntEnd    = new NumericTextField();

	public DefaultConfigurationPane(DefaultConfigurationSidebar sidebar) {
		this.sidebar = sidebar;
	}

	/*
	private void load() {
		ntDt.setValue(this.config.getParameter(StandardParameter.DT, 0));
		ntEnd.setValue(this.config.getParameter(StandardParameter.END, 0));
		ntStart.setValue(this.config.getParameter(StandardParameter.START, 0));
	}
	*/

	public void loadDataFromModel() {
		ntStart.setValue(sidebar.config.getParameter(StandardParameter.START, 0));
		ntEnd.setValue(sidebar.config.getParameter(StandardParameter.END, 0));
	}

	public void add() {
		sidebar.add(lblStart);
		sidebar.add(ntStart);
		ntStart.addFocusListener(this);

		sidebar.add(lblEnd);
		sidebar.add(ntEnd);
		ntEnd.addFocusListener(this);
	}

	public void remove() {
		sidebar.remove(lblStart);
		sidebar.remove(ntStart);
		ntStart.removeFocusListener(this);

		sidebar.remove(lblEnd);
		sidebar.remove(ntEnd);
		ntEnd.removeFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		try {
			sidebar.config.setParameter(StandardParameter.START, ntStart.getDoubleValue());
		} catch (ParseException ex) {
			ntStart.setValue(sidebar.config.getParameter(StandardParameter.START, 0));
		}
		try {
			sidebar.config.setParameter(StandardParameter.END, ntEnd.getDoubleValue());
		} catch (ParseException ex) {
			ntEnd.setValue(sidebar.config.getParameter(StandardParameter.END, 0));
		}
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		if (StandardParameter.START.equals(property)) {
			ntStart.setValue(newValue);
		}
		if (StandardParameter.END.equals(property)) {
			ntEnd.setValue(newValue);
		}
	}

	@Override
	public void propertyChanged(String property, String newValue) {
	}
}
