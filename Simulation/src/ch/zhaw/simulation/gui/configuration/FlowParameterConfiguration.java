package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.connection.FlowValve;

class FlowParameterConfiguration extends CodeConfiguration<FlowValve> {
	private static final long serialVersionUID = 1L;

	public FlowParameterConfiguration(SimulationControl control) {
		super(control, "Änderung", true);
	}
}