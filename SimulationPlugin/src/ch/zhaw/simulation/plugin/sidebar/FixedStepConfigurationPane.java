package ch.zhaw.simulation.plugin.sidebar;

import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationPane;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationSidebar;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.text.ParseException;

/**
 * @author: bachi
 */
public class FixedStepConfigurationPane extends DefaultConfigurationPane {

	private JLabel           lblDt = new JLabel("dt");
	private NumericTextField ntDt  = new NumericTextField();

	public FixedStepConfigurationPane(DefaultConfigurationSidebar sidebar) {
		super(sidebar);
	}

	@Override
	public void loadDataFromModel() {
		super.loadDataFromModel();

		ntDt.setValue(sidebar.config.getParameter(StandardParameter.DT, StandardParameter.DEFAULT_DT));
	}

	@Override
	public void addToSidebar() {
		super.addToSidebar();

		sidebar.add(lblDt);
		sidebar.add(ntDt);
		ntDt.addFocusListener(this);
	}

	@Override
	public void removeFromSidebar() {

		sidebar.remove(lblDt);
		sidebar.remove(ntDt);
		ntDt.removeFocusListener(this);

		super.removeFromSidebar();
	}

	@Override
	public void focusGained(FocusEvent e) {
		super.focusGained(e);
	}

	@Override
	public void focusLost(FocusEvent e) {
		super.focusLost(e);
		if (e.getSource() == ntDt) {
			try {
				sidebar.config.setParameter(StandardParameter.DT, ntDt.getDoubleValue());
			} catch (ParseException ex) {
				ntDt.setValue(sidebar.config.getParameter(StandardParameter.DT, StandardParameter.DEFAULT_DT));
			}
		}
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		super.propertyChanged(property, newValue);
		if (StandardParameter.DT.equals(property)) {
			ntDt.setValue(newValue);
		}
	}

	@Override
	public void propertyChanged(String property, String newValue) {
		super.propertyChanged(property, newValue);
	}
}
