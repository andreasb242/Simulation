package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.xy.DensityData;

public interface XYSimulationListener extends SimulationListener {
	public void densityAdded(DensityData d);
	public void densityRemoved(DensityData d);
	public void densityChanged(DensityData d);
}
