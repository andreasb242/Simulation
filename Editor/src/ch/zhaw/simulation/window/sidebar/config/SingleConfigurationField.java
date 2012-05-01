package ch.zhaw.simulation.window.sidebar.config;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

/**
 * A configuration field (or multiple fields) which supports editing of one
 * selected element
 * 
 * @see MultipleConfigurationField for multiple editing
 * 
 * @author Andreas Butti
 */
public abstract class SingleConfigurationField extends ConfigurationField {
	/**
	 * The currently selected data
	 */
	private AbstractNamedSimulationData data;

	public SingleConfigurationField() {
	}

	@Override
	public boolean supportsMultibleEditing() {
		return false;
	}

	@Override
	public void noneSelected() {
		hideComponents();
		this.data = null;
	}

	@Override
	public boolean dataSelected(AbstractNamedSimulationData data) {
		if (canHandleData(data)) {
			this.data = data;
			showComponents();
			loadData();

			return true;
		} else {
			noneSelected();
			return false;
		}
	}

	public AbstractNamedSimulationData getData() {
		return data;
	}

	@Override
	public boolean dataSelected(AbstractNamedSimulationData[] data) {
		// not implemented for single configuration
		return false;
	}

}
