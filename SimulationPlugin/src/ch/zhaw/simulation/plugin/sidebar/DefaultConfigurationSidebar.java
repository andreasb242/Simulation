package ch.zhaw.simulation.plugin.sidebar;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXTaskPane;

import butti.javalibs.numerictextfield.NumericTextField;
import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;
import ch.zhaw.simulation.plugin.StandardParameter;

public class DefaultConfigurationSidebar extends JXTaskPane implements FocusListener, SidebarPosition, SimulationParameterListener {
	private static final long serialVersionUID = 1L;

	private NumericTextField ntStart = new NumericTextField();
	private NumericTextField ntEnd = new NumericTextField();
	private NumericTextField ntDt = new NumericTextField();

	protected SimulationConfiguration config;

	public DefaultConfigurationSidebar(SimulationConfiguration config) {
		this.config = config;
		if (config == null) {
			throw new NullPointerException("config == null");
		}
		setTitle("Simulation Einstellungen");
		
		initComponents();
	}

	protected void initComponents() {
		add(new JLabel("Startzeit"));
		add(ntStart);
		ntStart.addFocusListener(this);

		add(new JLabel("Endzeit"));
		add(ntEnd);
		ntEnd.addFocusListener(this);

		add(new JLabel("dt"));
		add(ntDt);
		ntDt.addFocusListener(this);
	}

	public void load() {
		loadDataFromModel();
		config.addSimulationParameterListener(this);
	}

	public void unload() {
		config.removeSimulationParameterListener(this);
	}

	protected void loadDataFromModel() {
		ntDt.setValue(this.config.getParameter(StandardParameter.DT, 0));
		ntEnd.setValue(this.config.getParameter(StandardParameter.END, 0));
		ntStart.setValue(this.config.getParameter(StandardParameter.START, 0));
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		try {
			this.config.setParameter(StandardParameter.DT, ntDt.getDoubleValue());
		} catch (ParseException ex) {
			ntDt.setValue(this.config.getParameter(StandardParameter.DT, 0));
		}
		try {
			this.config.setParameter(StandardParameter.END, ntEnd.getDoubleValue());
		} catch (ParseException ex) {
			ntEnd.setValue(this.config.getParameter(StandardParameter.END, 0));
		}
		try {
			this.config.setParameter(StandardParameter.START, ntStart.getDoubleValue());
		} catch (ParseException ex) {
			ntStart.setValue(this.config.getParameter(StandardParameter.START, 0));
		}
	}

	@Override
	public int getSidebarPosition() {
		return 1000;
	}

	@Override
	public void propertyChanged(String property, double newValue) {
		if (StandardParameter.DT.equals(property)) {
			ntDt.setValue(newValue);
		}
		if (StandardParameter.END.equals(property)) {
			ntEnd.setValue(newValue);
		}
		if (StandardParameter.START.equals(property)) {
			ntStart.setValue(newValue);
		}
	}

	@Override
	public void propertyChanged(String property, String newValue) {
	}

	protected SimulationConfiguration getConfig() {
		return config;
	}
}
