package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.connection.FlowValve;

class FlowParameterConfiguration extends CodeConfiguration<FlowValve> {
	private static final long serialVersionUID = 1L;

	public FlowParameterConfiguration(FlowEditorControl control) {
		super(control, "Änderung", true);
	}
}