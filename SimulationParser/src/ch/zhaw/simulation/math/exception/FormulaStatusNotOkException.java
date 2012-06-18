package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.element.AbstractSimulationData;

public class FormulaStatusNotOkException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public FormulaStatusNotOkException(AbstractSimulationData simObject) {
		super(simObject, "Status von «" + getNameFor(simObject) + "» nicht OK, bitte Formel prüfen");
	}
}
