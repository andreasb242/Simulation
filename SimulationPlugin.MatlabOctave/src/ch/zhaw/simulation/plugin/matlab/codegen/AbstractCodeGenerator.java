package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import butti.javalibs.util.FileUtil;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.MatlabAttachment;
import ch.zhaw.simulation.plugin.matlab.MatlabVisitor;

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
		//
	}

	public void setWorkingFolder(String workingFolder) {
		this.workingFolder = workingFolder;
	}

	public String getWorkingFolder() {
		return workingFolder;
	}

	/**
	 * Extracts the Basefile for the simulation model
	 *
	 * @throws IOException
	 */
	protected void extractBaseFile() throws IOException {
		// TODO: wofür braucht man das? predefinedFiles ist ja leer!
		for (String file : predefinedFiles) {
			InputStream in = getClass().getResourceAsStream("files/" + file);

			FileUtil.copyFile(in, new File(this.workingFolder + File.separator + file));
		}
	}

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
	protected abstract void printSaveCurrentValues(CodeOutput out);
	protected abstract void printCloseFiles(CodeOutput out);

	protected abstract void printFlowCalculations(CodeOutput out);

	protected abstract void printContainerCalculations(CodeOutput out);
	
	protected abstract void printParameterCalculations(CodeOutput out);
}
