package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.SimulationContainer;

class ContainerConfiguration extends CodeConfiguration<SimulationContainer> {
	private static final long serialVersionUID = 1L;

	public ContainerConfiguration(FlowEditorControl control) {
		super(control, "Startwert", true);
	}
}