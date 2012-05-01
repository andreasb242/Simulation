package ch.zhaw.simulation.plugin.matlab.codegen;

/**
 * @author: bachi
 */
public abstract class AdaptiveMinorStepCodeGenerator extends AdaptiveStepCodeGenerator {

	@Override
	protected void printInitDeltaVector(CodeOutput out) {
		out.printComment("Initialize delta matrix");
		out.println("sim_k = zeros(length(sim_y),6);");
		out.println("sim_k(:,1) = sim_dy;");
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

		out.printComment("dy");
		out.println("sim_dynew = sim_k * sim_ha(:,5);");
		out.newline();

		out.printComment("y = y + dy");
		out.println("sim_ynew = sim_y + sim_dynew;");
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
