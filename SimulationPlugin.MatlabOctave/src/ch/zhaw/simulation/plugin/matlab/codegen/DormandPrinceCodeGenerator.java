package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

/**
 * @author: bachi
 */
public class DormandPrinceCodeGenerator extends DefaultCodeGenerator {

	@Override
	public void executeSimulation(SimulationDocument doc) throws Exception {
		System.out.println("DormandPrinceCodeGenerator");
	}

	@Override
	protected void printOpenFiles(CodeOutput out) {

	}

	@Override
	protected void printCloseFiles(CodeOutput out) {

	}

	@Override
	protected void printPredefinedConstants(CodeOutput out) {

	}

	@Override
	protected void printSaveCurrentValues(CodeOutput out) {

	}

	@Override
	protected void printContainerCalculations(CodeOutput out) {

	}
}
