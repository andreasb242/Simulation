package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

/**
 * @author: bachi
 */
public class CashKarpCodeGenerator extends AdaptiveStepCodeGenerator {

	@Override
	protected void printInitDeltaVector(CodeOutput out) {
		out.printComment("Initialize delta matrix");
		out.println("sim_k = zeros(length(sim_y), 6);");
		out.println("sim_k(:,1) = sim_dy;");
		out.newline();
	}

	@Override
	protected void printButcherTableau(CodeOutput out) {
		out.println("sim_a = [ 1/5    3/40    3/10    -11/54     1631/55296");
		out.println("            0    9/40   -9/10       5/2        175/512");
		out.println("            0       0     6/5    -70/27      575/13824");
		out.println("            0       0       0    -35/27   44275/110592");
		out.println("            0       0       0         0       253/4096");
		out.println("            0       0       0         0              0 ];");
		out.newline();

		out.println("sim_b = [ -277/64512; 0; 6925/370944; -6925/202752; -277/14336; 277/7084 ];");
		out.newline();

		out.println("sim_c = [ 1/5, 3/10, 3/5, 1, 7/8];");
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
