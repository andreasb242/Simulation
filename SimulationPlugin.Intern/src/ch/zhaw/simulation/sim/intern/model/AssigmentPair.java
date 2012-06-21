package ch.zhaw.simulation.sim.intern.model;

import org.nfunk.jep.ASTVarNode;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

public class AssigmentPair {
	private ASTVarNode node;
	private AbstractNamedSimulationData so;

	private boolean neverStatic = false;

	protected AssigmentPair() {
	}

	public boolean isNeverStatic() {
		return neverStatic;
	}

	public AssigmentPair(ASTVarNode node, AbstractNamedSimulationData so, boolean neverStatic) {
		if (so == null) {
			throw new NullPointerException("so == null");
		}
		if (node == null) {
			throw new NullPointerException("node == null");
		}

		this.node = node;
		this.so = so;
		this.neverStatic = neverStatic;
	}

	public ASTVarNode getNode() {
		return node;
	}

	public AbstractNamedSimulationData getSimulationObject() {
		return so;
	}
}