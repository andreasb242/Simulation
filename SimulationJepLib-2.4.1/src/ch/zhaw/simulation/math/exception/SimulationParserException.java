package ch.zhaw.simulation.math.exception;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.model.element.AbstractSimulationData;

public class SimulationParserException extends SimulationModelException {
	private static final long serialVersionUID = 1L;

	public SimulationParserException(AbstractSimulationData simObject, ParseException e) {
		super(simObject, e);
	}
}
