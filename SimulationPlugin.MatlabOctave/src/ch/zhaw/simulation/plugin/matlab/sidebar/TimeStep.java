package ch.zhaw.simulation.plugin.matlab.sidebar;

import ch.zhaw.simulation.plugin.matlab.codegen.addon.TimeStepCodeAddon;
import ch.zhaw.simulation.plugin.sidebar.DefaultConfigurationPane;

/**
 * @author: bachi
 */
public class TimeStep {

	private String name;
	private DefaultConfigurationPane pane;
	private TimeStepCodeAddon codeAddon;
	
	public TimeStep(String name, DefaultConfigurationPane pane, TimeStepCodeAddon codeAddon) {
		this.name = name;
		this.pane = pane;
		this.codeAddon = codeAddon;
	}

	public String getName() {
		return name;
	}

	public DefaultConfigurationPane getPane() {
		return pane;
	}

	public TimeStepCodeAddon getCodeAddon() {
		return codeAddon;
	}

	@Override
	public String toString() {
		return name;
	}
}
