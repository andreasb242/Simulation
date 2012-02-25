package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.SimulationParameter;

class ParameterConfiguration extends CodeConfiguration<SimulationParameter> {
	private static final long serialVersionUID = 1L;

	public ParameterConfiguration(FlowEditorControl control) {
		super(control, "Wert", true);
	}
}