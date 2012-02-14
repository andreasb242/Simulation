package ch.zhaw.simulation.gui.configuration;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.SimulationGlobal;

class GlobalConfiguration extends CodeConfiguration<SimulationGlobal> {
	private static final long serialVersionUID = 1L;

	public GlobalConfiguration(SimulationControl control) {
		super(control, "Wert", true);
	}
}