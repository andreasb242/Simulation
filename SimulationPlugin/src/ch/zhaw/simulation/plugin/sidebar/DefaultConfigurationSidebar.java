package ch.zhaw.simulation.plugin.sidebar;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.simulation.SimulationParameterListener;

public class DefaultConfigurationSidebar extends JXTaskPane implements FocusListener, SidebarPosition, SimulationParameterListener {
	private static final long serialVersionUID = 1L;

	protected SimulationConfiguration config;
	protected DefaultConfigurationPane selectedPane;

	public DefaultConfigurationSidebar(SimulationConfiguration config) {
		this.config = config;
		if (config == null) {
			throw new NullPointerException("config == null");
		}
		setTitle("Simulation Einstellungen");
		
		initComponents();
	}

	protected void initComponents() {
		selectedPane = new StepConfigurationPane(this);
		selectedPane.add();
	}

	public SimulationConfiguration getConfig() {
		return config;
	}

	public void load() {
		config.addSimulationParameterListener(selectedPane);
	}

	public void unload() {
		config.removeSimulationParameterListener(selectedPane);
	}

	protected void loadDataFromModel() {
		selectedPane.loadDataFromModel();
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
