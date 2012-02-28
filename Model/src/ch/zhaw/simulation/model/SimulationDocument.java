package ch.zhaw.simulation.model;

import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.xy.XYModel;

/**
 * This is the main Simulation document
 * 
 * 
 * @author Andreas Butti
 */
public class SimulationDocument {
	/**
	 * The type of the simulation
	 */
	private SimulationType type;

	/**
	 * The flow model if this.type == FLOW
	 */
	private SimulationFlowModel flowModel;

	/**
	 * The XY model if this.type == XY
	 */
	private XYModel xyModel;

	/**
	 * The configuration for the simulation
	 */
	private SimulationConfiguration simulationConfiguration = new SimulationConfiguration();

	public SimulationDocument() {
	}

	public void setType(SimulationType type) {
		// TODO !!!! fire xxxx

		if (type == SimulationType.FLOW_SIMULATION) {
			this.flowModel = new SimulationFlowModel();
			this.xyModel = null;
		} else {
			this.flowModel = null;
			this.xyModel = new XYModel();
		}

		this.type = type;
	}

	public SimulationType getType() {
		return type;
	}

	public XYModel getXyModel() {
		return xyModel;
	}

	public SimulationFlowModel getFlowModel() {
		return flowModel;
	}

	public AbstractSimulationModel<?> getModel() {
		if (type == SimulationType.FLOW_SIMULATION) {
			return flowModel;
		} else {
			return xyModel;
		}
	}

	/**
	 * @return true if the document has been changed since the last save
	 */
	public boolean isChanged() {
		return getModel().isChanged();
	}

	/**
	 * Sets the "saved" flag
	 */
	public void setSaved() {
		getModel().setSaved();
	}

	/**
	 * @return The simulation configuration
	 */
	public SimulationConfiguration getSimulationConfiguration() {
		return simulationConfiguration;
	}

	public void stopAutoparser() {
		// TODO Auto-generated method stub

	}

	public void startAutoparser() {
		// TODO Auto-generated method stub

	}

	public void clear() {
		getModel().clear();
	}
}
