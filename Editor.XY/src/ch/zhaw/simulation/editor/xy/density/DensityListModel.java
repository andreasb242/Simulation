package ch.zhaw.simulation.editor.xy.density;

import javax.swing.AbstractListModel;

import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.XYSimulationListener;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.XYModel;

public class DensityListModel extends AbstractListModel implements XYSimulationListener {
	private static final long serialVersionUID = 1L;
	private XYModel model;

	public DensityListModel(XYModel model) {
		this.model = model;
		model.addListener(this);
	}
	
	public void dispose() {
		model.removeListener(this);
	}
	
	@Override
	public int getSize() {
		return model.getDensity().size();
	}

	@Override
	public Object getElementAt(int index) {
		return model.getDensity().get(index);
	}

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
		int index = model.getDensity().indexOf(d);
		fireIntervalAdded(this, index, index);
	}

	@Override
	public void densityRemoved(DensityData d) {
		int index = model.getDensity().indexOf(d);
		fireIntervalRemoved(this, index, index);
	}

	@Override
	public void densityChanged(DensityData d) {
		int index = model.getDensity().indexOf(d);
		fireContentsChanged(this, index, index);
	}

}
