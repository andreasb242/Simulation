package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.SimulationGlobal;

class GlobalConfiguration extends CodeConfiguration<SimulationGlobal> {
	private static final long serialVersionUID = 1L;

	public GlobalConfiguration(FlowEditorControl control) {
		super(control, "Wert", true);
	}
}