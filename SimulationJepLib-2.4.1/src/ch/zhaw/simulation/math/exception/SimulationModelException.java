package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;

public class SimulationModelException extends Exception {
	private static final long serialVersionUID = 1L;

	private AbstractSimulationData simObject;

	public SimulationModelException(AbstractSimulationData simObject, String message) {
		super(message);
		this.simObject = simObject;
	}

	public SimulationModelException(AbstractSimulationData simObject) {
		this.simObject = simObject;
	}

	public SimulationModelException(AbstractSimulationData simObject, Throwable cause) {
		super(cause);
		this.simObject = simObject;
	}

	public SimulationModelException(AbstractSimulationData simObject, String message, Throwable cause) {
		super(message, cause);
		this.simObject = simObject;
	}

	public AbstractSimulationData getSimObject() {
		return simObject;
	}

	public static String getNameFor(AbstractSimulationData o) {
		if (o instanceof AbstractNamedSimulationData) {
			return ((AbstractNamedSimulationData) o).getName();
		}
		return o.getClass() + "/" + o.hashCode();
	}
}
