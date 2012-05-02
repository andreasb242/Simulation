package ch.zhaw.simulation.editor.density;

import javax.swing.DefaultComboBoxModel;

import ch.zhaw.simulation.model.listener.XYSimulationAdapter;
import ch.zhaw.simulation.model.listener.XYSimulationListener;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

public class DensityListModel extends DefaultComboBoxModel {
	private static final long serialVersionUID = 1L;
	private SimulationXYModel model;

	private XYSimulationListener simulationListener = new XYSimulationAdapter() {

		@Override
		public void clearData() {
			removeAllElements();
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
