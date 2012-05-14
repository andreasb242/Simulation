package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.plugin.matlab.FlowModelAttachment;

/**
 * Euler Code-Generator
 * 
 * @author Andreas Bachmann
 */
public class EulerCodeGenerator extends FixedStepCodeGenerator {

	private static final String FILENAME = "simulation_euler";

	/**
	 * @see AbstractCodeGenerator
	 */
	@Override
	public void generateSimulation(SimulationDocument doc) throws IOException {
		String outputFile;
		CodeOutput out;

		outputFile= getWorkingFolder() + File.separator + FILENAME + ".m";
		out = new CodeOutput(new FileOutputStream(outputFile));

		initSimulation(doc);
		printHeader(out);

		printInitDebug(out);

		printPredefinedConstants(out);
		printContainerInitialisation(out);
		printParameterInitialisation(out);
		printFlowCalculations(out);

		printOpenFiles(out);
		printValuesToFile(out);

		out.println(COUNT + " = ceil("+ END + " / " + DT + ") - ceil("+ START + " / " + DT + ");");
		out.newline();

		out.println("for i = 1 : sim_count + 1");
		out.indent();
		
		out.println("printf(\"%i\\n\", i);");

		printFlowCalculations(out);
		printContainerCalculations(out);
		printParameterCalculations(out);

		printValuesToFile(out);

		printDebug(out);

		out.println(TIME + " = " + TIME + " + " + DT + ";");
		out.newline();

		out.detent();
		out.println("end");
		out.newline();

		printCloseFiles(out);

		//printDebugGraph(out);
		out.println("exit");
		out.close();
	}

	@Override
	public String getGeneratedFile() {
		return FILENAME;
	}

	/**
	 * @see AbstractCodeGenerator
	 */
	protected void printContainerCalculations(CodeOutput out) {
		out.printComment("Container calculations");

		for (SimulationContainerData c : flowModel.getSimulationContainer()) {
			FlowModelAttachment a = (FlowModelAttachment) c.attachment;

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
