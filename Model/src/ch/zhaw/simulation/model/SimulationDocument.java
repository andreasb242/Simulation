package ch.zhaw.simulation.model;

import java.util.Vector;

import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

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
	private SimulationXYModel xyModel;

	/**
	 * The configuration for the simulation
	 */
	private SimulationConfiguration simulationConfiguration = new SimulationConfiguration();

	/**
	 * The autoparser enabled / disabled listener
	 */
	private Vector<AutoparserListener> autoparserListener = new Vector<AutoparserListener>();

	public SimulationDocument() {
	}

	public void setType(SimulationType type) {
		if (type == SimulationType.FLOW_SIMULATION) {
			this.flowModel = new SimulationFlowModel();
			this.xyModel = null;
		} else {
			this.flowModel = null;
			this.xyModel = new SimulationXYModel();
		}

		this.type = type;
	}

	public SimulationType getType() {
		return type;
	}

	public SimulationXYModel getXyModel() {
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
		for (AutoparserListener l : autoparserListener) {
			l.stopAutoparser();
		}
	}

	public void startAutoparser() {
		for (AutoparserListener l : autoparserListener) {
			l.startAutoparser();
		}
	}

	public void addListener(AutoparserListener l) {
		this.autoparserListener.add(l);
	}

	public void removeListener(AutoparserListener l) {
		this.autoparserListener.remove(l);
	}

	public void clear() {
		getModel().clear();
	}
}
