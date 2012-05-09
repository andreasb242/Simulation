package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.plugin.StandardParameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author: bachi
 */
public abstract class AdaptiveStepCodeGenerator extends FlowCodeGenerator {

	protected static final String FILENAME_MAIN = "simulation_adaptive";
	protected static final String FILENAME_ODE = "simulation_adaptive_ode";

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

		printAdaptiveStepMethodVariables(out);
		printButcherTableau(out);

		printInitialValueVector(out);

		out.printComment("Differential vector at the beginning");
		out.println("sim_dy = " + FILENAME_ODE + "(sim_time, sim_y);");
		out.newline();

		printInitDeltaVector(out);

		out.printComment("Initial-Schritt berechnen aus allen Anfangswerten");
		out.println("sim_hmax = " + config.getParameter(StandardParameter.MAX_STEP, StandardParameter.DEFAULT_MAX_STEP) + ";");
		out.println("sim_hfactor = " + config.getParameter(StandardParameter.H_FACTOR, StandardParameter.DEFAULT_H_FACTOR) + ";");
		out.println("sim_h = 1 / (norm(sim_dy ./ max(abs(sim_y), sim_err_threshold), inf) / (0.8 * sim_err_tolerance^sim_pow));");
		out.println("sim_finish = false;");
		out.println("while ~sim_finish");
		out.newline();
		out.indent();

		out.printComment("Kleinstmöglicher Schritt");
		out.println("sim_hmin = 16*eps(sim_time);");
		out.newline();

		out.printComment("Schritt zwischen sim_hmin und sim_hmax");
		out.println("sim_h    = min(sim_hmax, max(sim_hmin, sim_h));");
		out.newline();

		out.printComment("Shrink sim_h if it would overflow");
		out.println("if (sim_time + sim_h) > sim_end");
		out.indent();
		out.println("sim_h = sim_end - sim_time;");
		out.println("sim_finish = true;");
		out.detent();
		out.println("end;");
		out.newline();


		out.printComment("Variable sim_time und sim_y werden nicht verändert");
		out.printComment("Nur sim_h kann erniedrigt werden");
		out.println("sim_nofail = true;");
		out.println("while true");
		out.indent();

		out.println("sim_ha = sim_h * sim_a;");
		out.println("sim_hc = sim_h * sim_c;");
		out.newline();

		printContainerCalculations(out);

		printCalculateError(out);

		out.detent();
		out.println("end;");

		printIncrementStepSize(out);

		printSaveNewValues(out);
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

	protected abstract void printInitDeltaVector(CodeOutput out);

	protected void printAdaptiveStepMethodVariables(CodeOutput out) {
		out.printComment("Variables for the adaptive step method");
		out.printVariable("sim_err_tolerance", config.getParameter(StandardParameter.TOLERANCE, StandardParameter.DEFAULT_TOLERANCE));
		out.printVariable("sim_err_threshold", "0.001");
		out.printVariable("sim_pow", "1/5");
		out.newline();
	}

	protected abstract void printButcherTableau(CodeOutput out);

	protected void printCalculateError(CodeOutput out) {
		out.printComment("Fehler zwischen 4. und 5. Ordnung ausrechnen");
		out.println("sim_err = sim_h * norm((sim_k * sim_b) ./ max(max(abs(sim_y), abs(sim_ynew)), sim_err_threshold), inf);");
		out.newline();

		out.printComment("Guter Schritt");
		out.printComment("falls minimale Schrittweite erreicht wurde");
		out.printComment("oder Fehler noch in der Toleranz liegen, brich ab");
		out.println("if (sim_h <= sim_hmin || sim_err <= sim_err_tolerance)");
		out.indent();
		out.println("break;");
		out.detent();
		out.newline();

		out.printComment("Fehlerhafter Schritt");
		out.printComment("sim_h erniedrigen und die ganze Rechnung nochmals durchführen");
		out.println("else");
		out.indent();

		out.println("sim_finish = false;");
		out.println("if sim_nofail");
		out.indent();
		out.println("sim_nofail = false;");
		out.println("sim_h = max(sim_hmin, sim_h * max(0.1, 0.8*(sim_err_tolerance/sim_err)^sim_pow));");
		out.detent();
		out.println("else");
		out.indent();
		out.println("sim_h = max(sim_hmin, 0.5 * sim_h);");
		out.detent();
		out.println("end;");

		out.detent();
		out.println("end;");
		out.newline();
	}

	protected void printIncrementStepSize(CodeOutput out) {
		out.printComment("If this step hasn't been downscaled, scale it up");
		out.println("if sim_nofail");
		out.indent();
		out.println("sim_timeemp = sim_hfactor*(sim_err/sim_err_tolerance)^sim_pow;");
		out.println("if sim_timeemp > 0.2");
		out.indent();
		out.println("sim_h = sim_h / sim_timeemp;");
		out.detent();
		out.println("else");
		out.indent();
		out.println("sim_h = 5.0*sim_h;");
		out.detent();
		out.println("end;");
		out.detent();
		out.println("end;");
		out.newline();
	}

	protected abstract void printSaveNewValues(CodeOutput out);

	@Override
	protected void printPredefinedConstants(CodeOutput out) {
		out.printComment("Predefined constants");
		out.printVariable("sim_start", config.getParameter(StandardParameter.START, StandardParameter.DEFAULT_START));
		out.printVariable("sim_end", config.getParameter(StandardParameter.END, StandardParameter.DEFAULT_END));
		out.newline();

		out.printComment("Time variable");
		out.printVariable("sim_time", "sim_start");
		out.newline();
	}
}
