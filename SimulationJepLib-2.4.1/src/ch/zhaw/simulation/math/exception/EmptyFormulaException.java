package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.element.SimulationData;

public class EmptyFormulaException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public EmptyFormulaException(SimulationData o) {
		super(o, "Die Formel von " + getNameFor(o) + " ist leer!");
	}
}
