package ch.zhaw.simulation.math.exception;


public class NotUsedException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public NotUsedException(Object simObject, String unusedSource) {
		super(simObject, "Der Parameter " + unusedSource + " wird nicht verwendet in " + getNameFor(simObject) + "!");
	}
}
