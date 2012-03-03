package ch.zhaw.simulation.math.exception;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.model.element.SimulationData;

public class SimulationParserException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public SimulationParserException(SimulationData simObject, ParseException e) {
		super(simObject, e);
	}
}
