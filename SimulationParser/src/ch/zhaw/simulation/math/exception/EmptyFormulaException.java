package ch.zhaw.simulation.math.exception;

public class EmptyFormulaException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public EmptyFormulaException(Object simObject) {
		super(simObject, "Die Formel von " + getNameFor(simObject) + " ist leer!");
	}
}
