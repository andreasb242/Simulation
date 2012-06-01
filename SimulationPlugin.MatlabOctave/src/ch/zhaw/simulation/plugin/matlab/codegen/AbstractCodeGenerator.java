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
	public abstract void generateSimulation(SimulationDocument doc) throws Exception;

	protected abstract void initSimulation(SimulationDocument doc) throws IOException;

}
