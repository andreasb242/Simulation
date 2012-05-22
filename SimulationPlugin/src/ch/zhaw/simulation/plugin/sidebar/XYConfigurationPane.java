package ch.zhaw.simulation.plugin.sidebar;

import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.plugin.StandardParameter;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.text.ParseException;

/**
 * @author: bachi
 */
public class XYConfigurationPane extends FixedStepConfigurationPane {

	private JLabel           lblFrames = new JLabel("Bilder pro Zeiteinheit");
	private NumericTextField ntFrames  = new NumericTextField();

	private JLabel           lblDiffusion = new JLabel("Diffusionkoeffizient");
	private NumericTextField ntDiffusion  = new NumericTextField();

	public XYConfigurationPane(DefaultConfigurationSidebar sidebar) {
		super(sidebar);
	}

	@Override
	public void loadDataFromModel() {
		super.loadDataFromModel();

		ntFrames.setValue(sidebar.config.getParameter(StandardParameter.FRAMES, StandardParameter.DEFAULT_FRAMES));
		ntDiffusion.setValue(sidebar.config.getParameter(StandardParameter.DIFFUSION, StandardParameter.DEFAULT_DIFFUSION));
	}

	@Override
	public void add() {
		super.add();

		sidebar.add(lblFrames);
		sidebar.add(ntFrames);
		ntFrames.addFocusListener(this);

		sidebar.add(lblDiffusion);
		sidebar.add(ntDiffusion);
		ntDiffusion.addFocusListener(this);
	}

	@Override
	public void remove() {

		sidebar.remove(lblFrames);
		sidebar.remove(ntFrames);
		ntFrames.removeFocusListener(this);

		sidebar.remove(lblDiffusion);
		sidebar.remove(ntDiffusion);
		ntDiffusion.removeFocusListener(this);

		super.remove();
	}

	@Override
	public void focusGained(FocusEvent e) {
		super.focusGained(e);
	}

	@Override
	public void focusLost(FocusEvent e) {
		super.focusLost(e);

		if (e.getSource() == ntFrames) {
			try {
				sidebar.config.setParameter(StandardParameter.FRAMES, ntFrames.getDoubleValue());
			} catch (ParseException ex) {
				ntFrames.setValue(sidebar.config.getParameter(StandardParameter.FRAMES, StandardParameter.DEFAULT_FRAMES));
			}
		} else if (e.getSource() == ntDiffusion) {
			try {
				sidebar.config.setParameter(StandardParameter.DIFFUSION, ntDiffusion.getDoubleValue());
			} catch (ParseException ex) {
				ntDiffusion.setValue(sidebar.config.getParameter(StandardParameter.DIFFUSION, StandardParameter.DEFAULT_DIFFUSION));
			}
		}
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		super.propertyChanged(property, newValue);
		if (StandardParameter.FRAMES.equals(property)) {
			ntFrames.setValue(newValue);
		} else if (StandardParameter.DIFFUSION.equals(property)) {
			ntDiffusion.setValue(newValue);
		}
	}

	@Override
	public void propertyChanged(String property, String newValue) {
		super.propertyChanged(property, newValue);
	}
}
