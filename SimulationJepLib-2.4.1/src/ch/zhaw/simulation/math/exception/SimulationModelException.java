package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationData;

public class SimulationModelException extends Exception {
	private static final long serialVersionUID = 1L;

	private SimulationData simObject;

	public SimulationModelException(SimulationData simObject, String message) {
		super(message);
		this.simObject = simObject;
	}

	public SimulationModelException(SimulationData simObject) {
		this.simObject = simObject;
	}

	public SimulationModelException(SimulationData simObject, Throwable cause) {
		super(cause);
		this.simObject = simObject;
	}

	public SimulationModelException(SimulationData simObject, String message, Throwable cause) {
		super(message, cause);
		this.simObject = simObject;
	}

	public SimulationData getSimObject() {
		return simObject;
	}

	public static String getNameFor(SimulationData o) {
		if (o instanceof AbstractNamedSimulationData) {
			return ((AbstractNamedSimulationData) o).getName();
		}
		return o.getClass() + "/" + o.hashCode();
	}
}
