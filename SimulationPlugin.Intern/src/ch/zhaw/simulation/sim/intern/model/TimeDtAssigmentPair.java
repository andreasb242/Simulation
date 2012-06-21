package ch.zhaw.simulation.sim.intern.model;

import org.nfunk.jep.ASTVarNode;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

public class TimeDtAssigmentPair extends AssigmentPair {

	public TimeDtAssigmentPair() {
		super();
	}

	@Override
	public boolean isNeverStatic() {
		return true;
	}

	@Override
	public ASTVarNode getNode() {
		throw new RuntimeException("This method should not be called");
	}

	@Override
	public AbstractNamedSimulationData getSimulationObject() {
		throw new RuntimeException("This method should not be called");
	}
}