package ch.zhaw.simulation.math.exception;

import ch.zhaw.simulation.model.element.SimulationObject;

public class NotUsedException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public NotUsedException(SimulationObject o, String unusedSource) {
		super(o, "Der Parameter " + unusedSource + " wird nicht verwendet in " + getNameFor(o) + "!");
	}
}
