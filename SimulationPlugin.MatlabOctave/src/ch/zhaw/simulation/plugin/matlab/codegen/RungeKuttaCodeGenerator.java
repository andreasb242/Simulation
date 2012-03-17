package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author: bachi
 */
public class RungeKuttaCodeGenerator extends FixedStepCodeGenerator {

	private static final String FILENAME_MAIN = "simulation_rk_main.m";
	private static final String FILENAME_FUNCTION = "simulation_rk_func.m";

	@Override
	public void executeSimulation(SimulationDocument doc) throws Exception {
		CodeOutput main;
		CodeOutput func;

		main = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME_MAIN));
		func = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME_FUNCTION));

		initSimulation(doc);
		printHeader(main);
		printPredefinedConstants(main);
		printContainerInitialisation(main);
		printParameterInitialisation(main);
		printOpenFiles(main);

		main.println(COUNT + " = ceil("+ END + " / " + DT + ") - ceil("+ START + " / " + DT + ");");
		main.newline();

		// TODO: DEBUG
		main.println("% Debug only!");
		main.println("x = zeros(1, " + COUNT + ");");
		main.newline();

		main.println("for i = 1 : sim_count + 1");
		main.indent();

		printFlowCalculations(main);
		printContainerCalculations(main);
		printParameterCalculations(main);

		printSaveCurrentValues(main);

		// TODO: DEBUG
		main.println("% Debug only!");
		main.println("x(i)=UC.value;");
		main.newline();

		main.println(TIME + " = " + TIME + " + " + DT + ";");
		main.newline();

		main.detent();
		main.println("end");
		main.newline();

		printCloseFiles(main);

		// TODO: DEBUG
		main.println("% Debug only!");
		main.println("t = " + START + ":" + DT + ":" + END + ";");
		main.println("plot(t,x,'r','LineWidth',2);");
		main.println("legend('dt=" + dt + "',2);");
		main.println("title('Euler');");
		main.newline();

		main.close();
	}

	@Override
	protected void printContainerCalculations(CodeOutput out) {

	}
}
