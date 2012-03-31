package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.IOException;

import ch.zhaw.simulation.model.SimulationDocument;

/**
 * Class for Matlab / Octave Codegenration
 * 
 * @author Andreas Butti
 */
public abstract class AbstractCodeGenerator {

	/**
	 * Working folder for generated Matlab / Octave Code
	 */
	private String workingFolder;

	public AbstractCodeGenerator() {
		//
	}

	public void setWorkingFolder(String workingFolder) {
		this.workingFolder = workingFolder;
	}

	public String getWorkingFolder() {
		return workingFolder;
	}
	
	public abstract String getGeneratedFile();

	/**
	 * Create all simulation files to the working folder
	 *
	 * @param doc
	 *            The model to simulate
	 * @throws IOException
	 */
	public abstract void executeSimulation(SimulationDocument doc) throws Exception;


	protected abstract void initSimulation(SimulationDocument doc) throws IOException;

	protected abstract void printHeader(CodeOutput out);

	protected abstract void printPredefinedConstants(CodeOutput out);


	/**
	 * Print out container initialisation
	 *
	 * @param out file to write on
	 */
	protected abstract void printContainerInitialisation(CodeOutput out);

	/**
	 * Print out parameter initialisation
	 *
	 * @param out file to write on
	 */
	protected abstract void printParameterInitialisation(CodeOutput out);

	protected abstract void printOpenFiles(CodeOutput out);
	protected abstract void printValuesToFile(CodeOutput out);
	protected abstract void printCloseFiles(CodeOutput out);

	protected abstract void printFlowCalculations(CodeOutput out);

	protected abstract void printContainerCalculations(CodeOutput out);
	
	protected abstract void printParameterCalculations(CodeOutput out);
}
