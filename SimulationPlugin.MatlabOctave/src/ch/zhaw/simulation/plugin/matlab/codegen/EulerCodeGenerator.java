package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.plugin.matlab.MatlabAttachment;

/**
 * Euler Code-Generator
 * 
 * @author Andreas Bachmann
 */
public class EulerCodeGenerator extends FixedStepCodeGenerator {

	private static final String FILENAME = "simulation_euler.m";

	/**
	 * @see AbstractCodeGenerator
	 */
	@Override
	public void executeSimulation(SimulationDocument doc) throws IOException {
		String outputFile;
		CodeOutput out;

		outputFile= getWorkingFolder() + File.separator + FILENAME;
		out = new CodeOutput(new FileOutputStream(outputFile));

		initSimulation(doc);
		printHeader(out);
		printPredefinedConstants(out);
		printContainerInitialisation(out);
		printParameterInitialisation(out);
		printOpenFiles(out);

		out.println(COUNT + " = ceil("+ END + " / " + DT + ") - ceil("+ START + " / " + DT + ");");
		out.newline();

		// TODO: DEBUG
		out.println("% Debug only!");
		out.println("x = zeros(1, " + COUNT + ");");
		out.newline();

		out.println("for i = 1 : sim_count + 1");
		out.indent();

		printFlowCalculations(out);
		printContainerCalculations(out);
		printParameterCalculations(out);

		printValuesToFile(out);

		// TODO: DEBUG
		out.println("% Debug only!");
		out.println("x(i)=UC.value;");
		out.newline();

		out.println(TIME + " = " + TIME + " + " + DT + ";");
		out.newline();

		out.detent();
		out.println("end");
		out.newline();

		printCloseFiles(out);

		// TODO: DEBUG
		out.println("% Debug only!");
		out.println("t = " + START + ":" + DT + ":" + END + ";");
		out.println("plot(t,x,'r','LineWidth',2);");
		out.println("legend('dt=" + dt + "',2);");
		out.println("title('Euler');");
		out.newline();

		out.close();

		//Runtime.getRuntime().exec(new String[] { "gedit", outputFile });
	}

	/**
	 * @see AbstractCodeGenerator
	 */
	protected void printContainerCalculations(CodeOutput out) {
		out.printComment("Container calculations");

		for (SimulationContainerData c : flowModel.getSimulationContainer()) {
			MatlabAttachment a = (MatlabAttachment) c.attachment;

			// Only calculate non-constants
			if (!a.isConst()) {
				StringBuffer flows = new StringBuffer();
				for (FlowConnectorData f : flowModel.getFlowConnectors()) {
					if (f.getSource() == c) {
						flows.append("-");
						flows.append(f.getValve().getName() + ".value");
					}
					if (f.getTarget() == c) {
						if (flows.toString().length() != 0) {
							flows.append("+");
						}

						flows.append(f.getValve().getName() + ".value");
					}
				}

				out.println(c.getName() + ".value = " + c.getName() + ".value + " + flows.toString() + " * " + DT + ";");
			}
		}
		out.newline();
	}
}
