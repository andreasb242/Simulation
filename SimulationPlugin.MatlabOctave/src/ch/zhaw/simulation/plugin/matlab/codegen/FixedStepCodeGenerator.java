package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.matlab.MatlabAttachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Euler Code-Generator
 * 
 * @author Andreas Bachmann
 */
public abstract class FixedStepCodeGenerator extends DefaultCodeGenerator {

	protected static final String START = "sim_start";
	protected static final String END = "sim_end";
	protected static final String DT = "sim_dt";
	protected static final String TIME = "sim_time";
	protected static final String COUNT = "sim_count";

	protected Vector<AbstractNamedSimulationData> dataVector = new Vector<AbstractNamedSimulationData>();

	protected double start;
	protected double end;
	protected double dt;

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	@Override
	protected void initSimulation(SimulationDocument doc) throws IOException {
		super.initSimulation(doc);

		// Add container and parameter to a newly created vector
		// It will be used for file-handling
		dataVector.clear();
		dataVector.addAll(flowModel.getSimulationContainer());
		dataVector.addAll(flowModel.getSimulationParameter());
	}

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printPredefinedConstants(CodeOutput out) {
		// TODO: Defaultwerte störend?
		start = config.getParameter(StandardParameter.START, 0);
		end   = config.getParameter(StandardParameter.END, 5);
		dt    = config.getParameter(StandardParameter.DT, 0.01);

		out.printComment("Predefined constants");
		out.printVariable(START, start);
		out.printVariable(END, end);
		out.printVariable(DT, dt);
		out.newline();

		out.printComment("Time variable");
		out.printVariable(TIME, START);
		out.newline();
	}

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printOpenFiles(CodeOutput out) {
		out.printComment("Open output files");
		for (AbstractNamedSimulationData namedData : dataVector) {
			String var = namedData.getName() + ".fp";
			out.println(var + " = fopen('" + namedData.getName() + "_data.txt', 'w');");
		}
		out.newline();
	}

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printCloseFiles(CodeOutput out) {
		out.printComment("Close output files");
		for (AbstractNamedSimulationData namedData : dataVector) {
			String var = namedData.getName() + ".fp";
			out.println("fclose(" + var + ");");
		}
		out.newline();
	}

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printSaveCurrentValues(CodeOutput out) {
		out.printComment("Save calculations");

		for (AbstractNamedSimulationData namedData : dataVector) {
			String fp = namedData.getName() + ".fp";
			String value = namedData.getName() + ".value";
			out.println("fprintf(" + fp + ", '%f\\t%e\\n', " + TIME + ", " + value + ");");
		}
		out.newline();
	}
}
