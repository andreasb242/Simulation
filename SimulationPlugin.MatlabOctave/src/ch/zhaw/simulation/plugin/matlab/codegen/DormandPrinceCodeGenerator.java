package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;

/**
 * @author: bachi
 */
public class DormandPrinceCodeGenerator extends AdaptiveStepCodeGenerator {
	@Override
	protected void printInitDeltaVector(CodeOutput out) {
		out.printComment("Initialize delta matrix");
		out.println("sim_k = zeros(length(sim_y), 7);");
		out.println("sim_k(:,1) = sim_dy;");
		out.newline();
	}

	protected void printButcherTableau(CodeOutput out) {
		out.printComment("Bucher tableau");
		out.println("sim_a = [ 1/5    3/40   44/45   19372/6561    9017/3168      35/384");
		out.println("            0    9/40  -56/15  -25360/2187      -355/33           0");
		out.println("            0       0    32/9   64448/6561   46732/5247    500/1113");
		out.println("            0       0       0     -212/729       49/176     125/192");
		out.println("            0       0       0            0  -5103/18656  -2187/6784");
		out.println("            0       0       0            0            0       11/84");
		out.println("            0       0       0            0            0           0 ];");
		out.newline();

		out.println("sim_b = [ 71/57600; 0; -71/16695; 71/1920; -17253/339200; 22/525; -1/40 ];");
		out.newline();

		out.println("sim_c = [ 1/5, 3/10, 4/5, 8/9, 1, 1];");
		out.newline();
	}

	@Override
	protected void printContainerCalculations(CodeOutput out) {
		int size = flowModel.getSimulationContainer().size();

		out.println("sim_k(:,2) = " + FILENAME_ODE + "(sim_time + sim_hc(1), sim_y + sim_k * sim_ha(:,1));");
		out.println("sim_k(:,3) = " + FILENAME_ODE + "(sim_time + sim_hc(2), sim_y + sim_k * sim_ha(:,2));");
		out.println("sim_k(:,4) = " + FILENAME_ODE + "(sim_time + sim_hc(3), sim_y + sim_k * sim_ha(:,3));");
		out.println("sim_k(:,5) = " + FILENAME_ODE + "(sim_time + sim_hc(4), sim_y + sim_k * sim_ha(:,4));");
		out.println("sim_k(:,6) = " + FILENAME_ODE + "(sim_time + sim_hc(5), sim_y + sim_k * sim_ha(:,5));");
		out.newline();
		out.println("sim_timenew = sim_time + sim_hc(6);");
		out.newline();

		out.printComment("y = y + dy * t");
		for (int i = 1; i <= size; i++) {
			out.println("sim_ynew(" + i + ",:) = sim_y(" + i + ",:) + sim_k(" + i + ",:) * sim_ha(:,6);");
		}
		out.newline();

		out.println("sim_k(:,7) = " + FILENAME_ODE + "(sim_timenew, sim_ynew);");
		out.newline();
	}

	protected void printSaveNewValues(CodeOutput out) {
		out.printComment("Die neuen Werte sim_timenew, sim_ynew und sim_k entgültig speichern");
		out.println("sim_time = sim_timenew;");
		out.println("sim_y = sim_ynew;");
		out.println("sim_k(:,1) = sim_k(:,7);");
		out.newline();
	}

}
