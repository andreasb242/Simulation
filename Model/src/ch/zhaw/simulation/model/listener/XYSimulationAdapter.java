package ch.zhaw.simulation.model.listener;

import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.xy.DensityData;

public abstract class XYSimulationAdapter implements XYSimulationListener{

	@Override
	public void dataAdded(AbstractSimulationData o) {
	}

	@Override
	public void dataRemoved(AbstractSimulationData o) {
	}

	@Override
	public void dataChanged(AbstractSimulationData o) {
	}

	@Override
	public void dataSaved(boolean saved) {
	}

	@Override
	public void clearData() {
	}

	@Override
	public void densityAdded(DensityData d) {
	}

	@Override
	public void densityRemoved(DensityData d) {
	}

	@Override
	public void densityChanged(DensityData d) {
	}

}
