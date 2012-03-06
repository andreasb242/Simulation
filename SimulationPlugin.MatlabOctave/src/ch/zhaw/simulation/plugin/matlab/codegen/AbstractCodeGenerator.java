package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import butti.javalibs.util.FileUtil;
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

	/**
	 * Predefined files, needed by simulation
	 */
	protected String[] predefinedFiles = new String[] {};

	public AbstractCodeGenerator() {
	}

	public void setWorkingFolder(String workingFolder) {
		this.workingFolder = workingFolder;
	}

	public String getWorkingFolder() {
		return workingFolder;
	}

	/**
	 * Create all simulation files to the working folder
	 * 
	 * @param doc
	 *            The model to simulate
	 * @throws IOException
	 */
	public abstract void executeSimulation(SimulationDocument doc) throws Exception;

	/**
	 * Extracts the Basefile for the simulation model
	 * 
	 * @throws IOException
	 */
	protected void extractBaseFile() throws IOException {
		for (String file : predefinedFiles) {
			InputStream in = getClass().getResourceAsStream("files/" + file);

			FileUtil.copyFile(in, new File(this.workingFolder + File.separator + file));
		}
	}
}