package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.element.SimulationData;

public class VarNotFoundException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public VarNotFoundException(SimulationData o, String var) {
		super(o, "Variable «" + var + "» nicht gefunden in «" + getNameFor(o) + "»");
	}

	public VarNotFoundException(SimulationData o, String var, Throwable cause) {
		super(o, "Variable «" + var + "» nicht gefunden in «" + getNameFor(o) + "»", cause);
	}
}
