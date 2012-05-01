package ch.zhaw.simulation.window.sidebar.config;

import java.util.Vector;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

/**
 * A configuration field (or multiple fields) which supports editing of multiple
 * selected element at one time
 * 
 * @see SingleConfigurationField for single editing
 * 
 * @author Andreas Butti
 */
public abstract class MultipleConfigurationField extends ConfigurationField {
	/**
	 * The currently selected data
	 */
	private Vector<AbstractNamedSimulationData> data;

	public MultipleConfigurationField() {
	}

	@Override
	public boolean supportsMultibleEditing() {
		return true;
	}

	@Override
	public void noneSelected() {
		hideComponents();
		data = null;
	}

	@Override
	public boolean dataSelected(AbstractNamedSimulationData data) {
		return dataSelected(new AbstractNamedSimulationData[] { data });
	}

	@Override
	public boolean dataSelected(AbstractNamedSimulationData[] data) {
		boolean supportAll = true;

		for (AbstractNamedSimulationData d : data) {
			if (!canHandleData(d)) {
				supportAll = false;
				break;
			}
		}

		if (supportAll) {
			this.data = new Vector<AbstractNamedSimulationData>();
			for (AbstractNamedSimulationData d : data) {
				this.data.add(d);
			}
			loadData();
			showComponents();
			return true;
		} else {
			noneSelected();
			return false;
		}
	}

	public Vector<AbstractNamedSimulationData> getData() {
		return data;
	}
}
