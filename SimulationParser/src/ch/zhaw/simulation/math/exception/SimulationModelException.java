package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.NamedFormulaData;

public class SimulationModelException extends Exception {
	private static final long serialVersionUID = 1L;

	private Object simObject;

	public SimulationModelException(Object simObject, String message) {
		super(message);
		this.simObject = simObject;
	}

	public SimulationModelException(Object simObject) {
		this.simObject = simObject;
	}

	public SimulationModelException(Object simObject, Throwable cause) {
		super(cause);
		this.simObject = simObject;
	}

	public SimulationModelException(Object simObject, String message, Throwable cause) {
		super(message, cause);
		this.simObject = simObject;
	}

	public Object getSimObject() {
		return simObject;
	}

	public static String getNameFor(Object o) {
		if (o instanceof NamedFormulaData) {
			return ((NamedFormulaData) o).getName();
		}
		return o.getClass() + "/" + o.hashCode();
	}
}
