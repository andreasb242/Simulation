package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationObject;

public class SimulationModelException extends Exception {
	private static final long serialVersionUID = 1L;

	private SimulationObject simObject;

	public SimulationModelException(SimulationObject simObject, String message) {
		super(message);
		this.simObject = simObject;
	}

	public SimulationModelException(SimulationObject simObject) {
		this.simObject = simObject;
	}

	public SimulationModelException(SimulationObject simObject, Throwable cause) {
		super(cause);
		this.simObject = simObject;
	}

	public SimulationModelException(SimulationObject simObject, String message, Throwable cause) {
		super(message, cause);
		this.simObject = simObject;
	}

	public SimulationObject getSimObject() {
		return simObject;
	}

	public static String getNameFor(SimulationObject o) {
		if (o instanceof NamedSimulationObject) {
			return ((NamedSimulationObject) o).getName();
		}
		return o.getClass() + "/" + o.hashCode();
	}
}
