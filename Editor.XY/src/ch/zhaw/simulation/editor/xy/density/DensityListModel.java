package ch.zhaw.simulation.editor.xy.density;

import javax.swing.DefaultComboBoxModel;

import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.listener.XYSimulationListener;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class DensityListModel extends DefaultComboBoxModel {
	private static final long serialVersionUID = 1L;
	private SimulationXYModel model;

	private XYSimulationListener simulationListener = new XYSimulationListener() {
		
		@Override
		public void dataSaved(boolean saved) {
		}
		
		@Override
		public void dataRemoved(AbstractSimulationData o) {
		}
		
		@Override
		public void dataChanged(AbstractSimulationData o) {
		}
		
		@Override
		public void dataAdded(AbstractSimulationData o) {
		}
		
		@Override
		public void clearData() {
			removeAllElements();
		}
		
		@Override
		public void modelSizeRasterChanged() {
		}
		
		@Override
		public void densityRemoved(DensityData d) {
			removeElement(d);
		}
		
		@Override
		public void densityChanged(DensityData d) {
			int index = getIndexOf(d);
			fireContentsChanged(this, index, index);
		}
		
		@Override
		public void densityAdded(DensityData d) {
			addElement(d);
		}
	};
	
	public DensityListModel(SimulationXYModel model) {
		this.model = model;
		this.model.addListener(simulationListener);
	}

	public void dispose() {
		model.removeListener(simulationListener);
	}
}
