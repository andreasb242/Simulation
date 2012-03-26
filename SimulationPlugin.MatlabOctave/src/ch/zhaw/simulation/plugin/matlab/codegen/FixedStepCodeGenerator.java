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

	protected static final String DT = "sim_dt";
	protected static final String COUNT = "sim_count";

	protected double start;
	protected double end;
	protected double dt;


	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printPredefinedConstants(CodeOutput out) {
		start = config.getParameter(StandardParameter.START, StandardParameter.DEFAULT_START);
		end   = config.getParameter(StandardParameter.END, StandardParameter.DEFAULT_END);
		dt    = config.getParameter(StandardParameter.DT, StandardParameter.DEFAULT_DT);

		out.printComment("Predefined constants");
		out.printVariable(START, start);
		out.printVariable(END, end);
		out.newline();

		out.printComment("Time variable");
		out.printVariable(TIME, START);
		out.printVariable(DT, dt);
		out.newline();
	}

}
