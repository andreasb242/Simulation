package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

/**
 * @author: bachi
 */
public class FehlbergCodeGenerator extends AdaptiveStepCodeGenerator {

	@Override
	protected void printInitDeltaVector(CodeOutput out) {
		out.printComment("Initialize delta matrix");
		out.println("sim_k = zeros(length(sim_y),6);");
		out.println("sim_k(:,1) = sim_dy;");
		out.newline();
	}

	@Override
	protected void printButcherTableau(CodeOutput out) {
		out.println("sim_a = [ 1/4    3/32   1932/2197     439/216        -8/27");
		out.println("            0    9/32  -7200/2197          -8            2");
		out.println("            0       0   7296/2197    3680/513   -3544/2565");
		out.println("            0       0           0   -845/4104    1859/4104");
		out.println("            0       0           0           0       -11/40");
		out.println("            0       0           0           0            0 ];");
		out.newline();

		out.println("sim_b = [ -1/360; 0; 128/4275; 2197/75240; -1/50; -22/55 ];");
		out.newline();

		out.println("sim_c = [ 1/4, 3/8, 12/13, 1, 1/2];");
		out.newline();
	}

	@Override
	protected void printContainerCalculations(CodeOutput out) {
		int size = flowModel.getSimulationContainer().size();

		out.println("sim_k(:,2) = " + FILENAME_ODE + "(sim_time + sim_hc(1), sim_y + sim_k * sim_ha(:,1));");
		out.println("sim_k(:,3) = " + FILENAME_ODE + "(sim_time + sim_hc(2), sim_y + sim_k * sim_ha(:,2));");
		out.println("sim_k(:,4) = " + FILENAME_ODE + "(sim_time + sim_hc(3), sim_y + sim_k * sim_ha(:,3));");
		out.println("sim_k(:,5) = " + FILENAME_ODE + "(sim_time + sim_hc(4), sim_y + sim_k * sim_ha(:,4));");
		out.newline();
		out.println("sim_timenew = sim_time + sim_hc(5);");
		out.newline();

		out.printComment("y = y + dy * t");
		for (int i = 1; i <= size; i++) {
			out.println("sim_ynew(" + i + ",:) = sim_y(" + i + ",:) + sim_k(" + i + ",:) * sim_ha(:,5);");
		}
		out.newline();

		out.println("sim_k(:,6) = " + FILENAME_ODE + "(sim_timenew, sim_ynew);");
		out.newline();
	}

	protected void printSaveNewValues(CodeOutput out) {
		out.printComment("Die neuen Werte sim_timenew, sim_ynew und sim_k entgültig speichern");
		out.println("sim_time = sim_timenew;");
		out.println("sim_y = sim_ynew;");
		out.println("sim_k(:,1) = sim_k(:,6);");
		out.newline();
	}
}
