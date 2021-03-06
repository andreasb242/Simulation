package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author: bachi
 */
public class RungeKuttaCodeGenerator extends FixedStepCodeGenerator {

	private static final String FILENAME_MAIN = "simulation_rk_main";
	private static final String FILENAME_ODE = "simulation_rk_ode";

	@Override
	public void generateSimulation(SimulationDocument doc) throws Exception {
		initSimulation(doc);
		saveSimulationMain();
		saveSimulationDifferential();
	}

	@Override
	public String getGeneratedFile() {
		return FILENAME_MAIN;
	}

	protected void saveSimulationMain() throws FileNotFoundException {
		CodeOutput out;

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME_MAIN + ".m"));

		printHeader(out);

		printGlobal(out);

		//printInitDebug(out);

		printPredefinedConstants(out);
		printContainerInitialisation(out);
		printParameterInitialisation(out);
		printFlowCalculations(out);

		printOpenFiles(out);
		printValuesToFile(out);

		printButcherTableau(out);

		printInitialValueVector(out);

		out.println("sim_count = ceil(sim_end / sim_dt);");
		out.newline();
		out.println("for i = 1:sim_count");
		out.indent();
		printContainerCalculations(out);
		printTimeStep(out);
		printVectorToContainer(out);
		printValuesToFile(out);
		//printDebug(out);
		out.detent();
		out.println("end;");

		printCloseFiles(out);

		//printDebugGraph(out);
		out.println("exit");
		out.close();
	}


	protected void saveSimulationDifferential() throws FileNotFoundException {
		CodeOutput out;

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME_ODE + ".m"));

		out.printComment("Flow calculation");
		out.println("function [ sim_dy ] = " + FILENAME_ODE + "(sim_time, sim_y)");
		out.indent();

		printGlobal(out);
		printVectorToContainer(out);
		printParameterCalculations(out);
		printFlowCalculations(out);
		printFlowToDifferential(out);

		out.detent();
		out.println("end");

		out.close();
	}

	@Override
	protected void printContainerCalculations(CodeOutput out) {
		out.printComment("Reset intermediate steps");
		out.println("sim_k = zeros(length(sim_y), 4);");
		out.println("sim_k(:,1) = " + FILENAME_ODE + "(sim_time + sim_dt * sim_c(1), sim_y + sim_dt * sim_k * sim_a(:,1));");
		out.println("sim_k(:,2) = " + FILENAME_ODE + "(sim_time + sim_dt * sim_c(2), sim_y + sim_dt * sim_k * sim_a(:,2));");
		out.println("sim_k(:,3) = " + FILENAME_ODE + "(sim_time + sim_dt * sim_c(3), sim_y + sim_dt * sim_k * sim_a(:,3));");
		out.println("sim_k(:,4) = " + FILENAME_ODE + "(sim_time + sim_dt * sim_c(4), sim_y + sim_dt * sim_k * sim_a(:,4));");
		out.newline();

		out.printComment("dy");
		out.println("sim_dy = sim_k * sim_b;");
		out.newline();

		out.printComment("y = y + dt * dy");
		out.println("sim_y = sim_y + sim_dt * sim_dy;");
		out.newline();

	}

	protected void printButcherTableau(CodeOutput out) {
		out.printComment("Bucher tableau");
		out.println("sim_a = [ 0   1/2     0   0");
		out.println("          0     0   1/2   0");
		out.println("          0     0     0   1");
		out.println("          0     0     0   0 ];");
		out.newline();

		out.println("sim_b = [ 1/6; 1/3; 1/3; 1/6 ];");
		out.newline();

		out.println("sim_c = [ 0 1/2 1/2 1 ];");
		out.newline();
	}

	/*
	@Override
	protected void printDebug(CodeOutput out) {
		int size = flowModel.getSimulationContainer().size();

		out.printComment("DEBUG");
		out.println("tmp_y(1, tmp_idx) = sim_time;");
		for (int i = 1; i <= size; i++) {
			out.println("tmp_y(" + (i + 1) + ", tmp_idx) = sim_y(" + i + ",1);");
		}
		out.println("tmp_idx = tmp_idx + 1;");
		out.newline();
	}
	*/
}
