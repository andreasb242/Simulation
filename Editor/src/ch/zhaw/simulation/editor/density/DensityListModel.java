package ch.zhaw.simulation.editor.density;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import ch.zhaw.simulation.model.listener.XYSimulationAdapter;
import ch.zhaw.simulation.model.listener.XYSimulationListener;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class DensityListModel extends AbstractListModel implements ComboBoxModel {
	private static final long serialVersionUID = 1L;
	private SimulationXYModel model;

	private Object selectedItem = null;

	private XYSimulationListener simulationListener = new XYSimulationAdapter() {

		@Override
		public void clearData() {
			fireContentsChanged(this, 0, getSize());
		}

		@Override
		public void densityRemoved(DensityData d) {
			fireContentsChanged(this, 0, getSize());
		}

		@Override
		public void densityChanged(DensityData d) {
			fireContentsChanged(this, 0, getSize());
		}

		@Override
		public void densityAdded(DensityData d) {
			int i = model.getDensity().indexOf(d);
			fireIntervalAdded(this, i, i);
		}
	};

	public DensityListModel(SimulationXYModel model) {
		this.model = model;
		this.model.addListener(simulationListener);

		if (this.model.getDensity().size() > 0) {
			this.selectedItem = this.model.getDensity().firstElement();
		}
	}

	public void dispose() {
		model.removeListener(simulationListener);
	}

	@Override
	public int getSize() {
		return this.model.getDensity().size();
	}

	@Override
	public Object getElementAt(int index) {
		return this.model.getDensity().get(index);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedItem = anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}
}
