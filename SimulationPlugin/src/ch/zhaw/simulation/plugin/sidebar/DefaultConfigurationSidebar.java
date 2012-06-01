package ch.zhaw.simulation.plugin.sidebar;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import ch.zhaw.simulation.model.SimulationType;
import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;

public class DefaultConfigurationSidebar extends JXTaskPane implements FocusListener, SidebarPosition, SimulationParameterListener {
	private static final long serialVersionUID = 1L;

	protected SimulationConfiguration config;
	protected DefaultConfigurationPane pane;
	protected SimulationType type;

	public DefaultConfigurationSidebar(SimulationConfiguration config, SimulationType type) {
		this.config = config;
		this.type = type;
		if (config == null) {
			throw new NullPointerException("config == null");
		}
		setTitle("Simulation Einstellungen");

		initComponents();
	}

	public SimulationType getType() {
		return type;
	}
	
	protected void initComponents() {
		pane = new FixedStepConfigurationPane(this);
		pane.addToSidebar();
	}

	public SimulationConfiguration getConfig() {
		return config;
	}

	public void load() {
		loadDataFromModel();
		config.addSimulationParameterListener(pane);
	}

	public void unload() {
		config.removeSimulationParameterListener(pane);
	}

	protected void loadDataFromModel() {
		pane.loadDataFromModel();
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
	}

	@Override
	public int getSidebarPosition() {
		return 1000;
	}

	@Override
	public void propertyChanged(String property, double newValue) {

	}

	@Override
	public void propertyChanged(String property, String newValue) {

	}
}
