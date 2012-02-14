package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;

class FlowParameterConfiguration extends CodeConfiguration<FlowParameterPoint> {
	private static final long serialVersionUID = 1L;

	public FlowParameterConfiguration(SimulationControl control) {
		super(control, "Änderung", true);
	}
}