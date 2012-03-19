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

	private JLabel           lblHFactor   = new JLabel("Schritt-Erhöhungsfaktor");
	private NumericTextField ntHFactor    = new NumericTextField();

	private JLabel           lblMaxStep   = new JLabel("Maximaler Schritt");
	private NumericTextField ntMaxStep    = new NumericTextField();

	private JLabel           lblTolerance = new JLabel("Fehlertoleranz");
	private NumericTextField ntTolerance  = new NumericTextField();

	public AdaptiveStepConfigurationPane(DefaultConfigurationSidebar sidebar) {
		super(sidebar);
	}

	@Override
	public void loadDataFromModel() {
		super.loadDataFromModel();
		ntHFactor.setValue(sidebar.config.getParameter(StandardParameter.H_FACTOR, StandardParameter.DEFAULT_H_FACTOR));
		ntMaxStep.setValue(sidebar.config.getParameter(StandardParameter.MAX_STEP, StandardParameter.DEFAULT_MAX_STEP));
		ntTolerance.setValue(sidebar.config.getParameter(StandardParameter.TOLERANCE, StandardParameter.DEFAULT_TOLERANCE));
	}

	@Override
	public void add() {
		super.add();
		sidebar.add(lblHFactor);
		sidebar.add(ntHFactor);
		ntHFactor.addFocusListener(this);

		sidebar.add(lblMaxStep);
		sidebar.add(ntMaxStep);
		ntMaxStep.addFocusListener(this);

		sidebar.add(lblTolerance);
		sidebar.add(ntTolerance);
		ntTolerance.addFocusListener(this);
	}

	@Override
	public void remove() {
		sidebar.remove(lblHFactor);
		sidebar.remove(ntHFactor);
		ntHFactor.removeFocusListener(this);

		sidebar.remove(lblMaxStep);
		sidebar.remove(ntMaxStep);
		ntMaxStep.removeFocusListener(this);

		sidebar.remove(lblTolerance);
		sidebar.remove(ntTolerance);
		ntTolerance.removeFocusListener(this);

		super.remove();
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		super.focusLost(e);
		if (e.getSource() == ntHFactor) {
			try {
				sidebar.config.setParameter(StandardParameter.H_FACTOR, ntHFactor.getDoubleValue());
			} catch (ParseException ex) {
				ntHFactor.setValue(sidebar.config.getParameter(StandardParameter.H_FACTOR, StandardParameter.DEFAULT_H_FACTOR));
			}
		} else if (e.getSource() == ntMaxStep) {
			try {
				sidebar.config.setParameter(StandardParameter.MAX_STEP, ntMaxStep.getDoubleValue());
			} catch (ParseException ex) {
				ntMaxStep.setValue(sidebar.config.getParameter(StandardParameter.MAX_STEP, StandardParameter.DEFAULT_MAX_STEP));
			}
		}else if (e.getSource() == ntTolerance) {
			try {
				sidebar.config.setParameter(StandardParameter.TOLERANCE, ntTolerance.getDoubleValue());
			} catch (ParseException ex) {
				ntTolerance.setValue(sidebar.config.getParameter(StandardParameter.TOLERANCE, StandardParameter.DEFAULT_TOLERANCE));
			}
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
