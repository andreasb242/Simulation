package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.SimulationParameter;

class ParameterConfiguration extends CodeConfiguration<SimulationParameter> {
	private static final long serialVersionUID = 1L;

	public ParameterConfiguration(SimulationControl control) {
		super(control, "Wert", true);
	}
}