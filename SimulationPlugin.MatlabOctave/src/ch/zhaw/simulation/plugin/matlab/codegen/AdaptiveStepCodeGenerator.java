package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.matlab.MatlabAttachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * @author: bachi
 */
public abstract class AdaptiveStepCodeGenerator extends DefaultCodeGenerator {

	private static final String FILENAME_MAIN = "simulation_adaptive";
	private static final String FILENAME_ODE = "simulation_adaptive_ode";

	protected static final String START = "sim_start";
	protected static final String END = "sim_end";
	protected static final String DT = "sim_dt";
	protected static final String TIME = "sim_time";
	protected static final String COUNT = "sim_count";

	@Override
	public void executeSimulation(SimulationDocument doc) throws Exception {
		initSimulation(doc);
		saveSimulationMain();
		saveSimulationDifferential();;
	}

	protected void saveSimulationMain() throws FileNotFoundException {
		CodeOutput out;

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME_MAIN + ".m"));

		out.printComment("Generated file by Simulation");
		out.newline();

		out.printComment("Cleanup");
		out.println("clc; clear all; close all;");

		printGlobal(out);

		printInitDebug(out);

		printPredefinedConstants(out);
		printContainerInitialisation(out);
		printParameterInitialisation(out);
		printFlowCalculations(out);

		printAdaptiveStepMethodVariables(out);
		printButcherTableau(out);

		printInitialValueVector(out);

		out.printComment("Differential vector at the beginning");
		out.println("sim_dy = " + FILENAME_ODE + "(sim_time, sim_y);");
		out.newline();
		
		out.printComment("Initialize delta matrix");
		out.println("sim_k = zeros(length(sim_y), 7);");
		out.println("sim_k(:,1) = sim_dy;");
		out.newline();

		out.printComment("Initial-Schritt berechnen aus allen Anfangswerten");
		out.println("sim_hmax = " + config.getParameter(StandardParameter.MAX_STEP, 0.5) + ";");
		out.println("sim_hfactor = " + config.getParameter(StandardParameter.H_FACTOR, 2) + ";");
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

		out.println("sim_k(:,2) = " + FILENAME_ODE + "(sim_time + sim_hc(1), sim_y + sim_k * sim_ha(:,1));");
		out.println("sim_k(:,3) = " + FILENAME_ODE + "(sim_time + sim_hc(2), sim_y + sim_k * sim_ha(:,2));");
		out.println("sim_k(:,4) = " + FILENAME_ODE + "(sim_time + sim_hc(3), sim_y + sim_k * sim_ha(:,3));");
		out.println("sim_k(:,5) = " + FILENAME_ODE + "(sim_time + sim_hc(4), sim_y + sim_k * sim_ha(:,4));");
		out.println("sim_k(:,6) = " + FILENAME_ODE + "(sim_time + sim_hc(5), sim_y + sim_k * sim_ha(:,5));");
		out.newline();
		out.println("sim_timenew = sim_time + sim_hc(6);");
		out.newline();

		printContainerCalculations(out);

		out.println("sim_k(:,7) = " + FILENAME_ODE + "(sim_timenew, sim_ynew);");

		printCalculateError(out);

		out.detent();
		out.println("end;");

		printIncrementStepSize(out);

		out.printComment("Die neuen Werte sim_timenew, sim_ynew und sim_k entgültig speichern");
		out.println("sim_time = sim_timenew;");
		out.println("sim_y = sim_ynew;");
		out.println("sim_k(:,1) = sim_k(:,7);");
		out.newline();

		printDebug(out);
		
		out.detent();
		out.println("end;");

		printDebugGraph(out);
		out.close();
	}

	protected void saveSimulationDifferential() throws FileNotFoundException {
		CodeOutput out;

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME_ODE + ".m"));

		out.printComment("Flow calculation");
		out.println("function [ dy ] = " + FILENAME_ODE + "(t, y)");
		out.indent();

		printGlobal(out);
		printDependentToContainer(out);
		printParameterCalculations(out);
		printFlowCalculations(out);
		printFlowToDifferential(out);

		out.detent();
		out.println("end");

		out.close();
	}

	/**
	 * Print global variables. It prints only constant parameters.
	 *
	 * @param out
	 */
	protected void printGlobal(CodeOutput out) {
		StringBuilder builder = new StringBuilder();
		Vector<SimulationParameterData> parameters = flowModel.getSimulationParameter();
		boolean isEmpty = true;


		builder.append("% Global constant parameters\n");
		builder.append("global");
		for (SimulationParameterData parameter : parameters) {
			MatlabAttachment attachment = (MatlabAttachment) parameter.attachment;

			if (attachment.isConst()) {
				isEmpty = false;
				builder.append(" ");
				builder.append(parameter.getName());
			}
		}

		if (!isEmpty) {
			out.println(builder.toString());
			out.newline();
		}
	}


	/**
	 * Print initial value vector. It prints only containers.
	 *
	 * @param out
	 */
	protected void printInitialValueVector(CodeOutput out) {
		StringBuilder builder = new StringBuilder();
		Vector<SimulationContainerData> containers = flowModel.getSimulationContainer();
		boolean isEmpty = true;

		builder.append("% Initial value vector\n");
		builder.append("sim_y = [");
		for (SimulationContainerData container : containers) {
			// to separate between vector elements
			// first element has no prefix
			if (!isEmpty) {
				builder.append(";");
			}

			isEmpty = false;
			builder.append(" ");
			builder.append(container.getName() + ".value");
		}
		builder.append(" ];");

		if (!isEmpty) {
			out.println(builder.toString());
			out.newline();
		}
	}

	protected void printAdaptiveStepMethodVariables(CodeOutput out) {
		out.printComment("Variables for the adaptive step method");
		out.printVariable("sim_err_tolerance", "0.001");
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

	protected void printInitDebug(CodeOutput out) {
		int size = flowModel.getSimulationContainer().size();

		out.printComment("DEBUG");
		out.println("tmp_idx = 1;");
		out.println("tmp_y = zeros(" + (size + 1) + ",1000);");
	}

	protected void printDebug(CodeOutput out) {
		int size = flowModel.getSimulationContainer().size();

		out.printComment("DEBUG");
		out.println("tmp_y(1, tmp_idx) = sim_timenew;");
		for (int i = 1; i <= size; i++) {
			out.println("tmp_y(" + (i + 1) + ", tmp_idx) = sim_ynew(" + i + ",1);");
		}
		out.println("tmp_idx = tmp_idx + 1;");
		out.newline();
	}

	protected void printDebugGraph(CodeOutput out) {
		int size = flowModel.getSimulationContainer().size();

		out.printComment("DEBUG");
		out.println("tmp_res = tmp_y(:, 1:tmp_idx);");
		for (int i = 1; i <= size; i++) {
			out.println("figure(" + i + ");");
			out.println("stem(tmp_res(1,:), tmp_res(" + (i + 1) + ",:));");
		}
		out.newline();
	}

	protected void printDependentToContainer(CodeOutput out) {
		Vector<SimulationContainerData> containers = flowModel.getSimulationContainer();
		int size = containers.size();

		out.printComment("Calculate / Interpolate");
		for (int i = 1; i <= size; i++) {
			SimulationContainerData container = containers.get(i - 1);
			out.println(container.getName() + ".value = y(" + i + ");");
		}
		out.newline();
	}

	protected void printFlowToDifferential(CodeOutput out) {
		Vector<SimulationContainerData> containers = flowModel.getSimulationContainer();
		int size = containers.size();

		out.println("dy      = zeros(" + size + ", 1);");
		for (int i = 1; i <= size; i++) {
			SimulationContainerData container = containers.get(i - 1);

			StringBuffer flows = new StringBuffer();
			for (FlowConnectorData f : flowModel.getFlowConnectors()) {
				if (f.getSource() == container) {
					flows.append("-");
					flows.append(f.getValve().getName() + ".value");
				}
				if (f.getTarget() == container) {
					if (flows.toString().length() != 0) {
						flows.append("+");
					}

					flows.append(f.getValve().getName() + ".value");
				}
			}

			out.println("dy(" + i + ",1) = " + flows.toString() + ";");
		}
	}

	@Override
	protected void printPredefinedConstants(CodeOutput out) {
		out.printComment("Predefined constants");
		out.printVariable("sim_start", config.getParameter(StandardParameter.START, 0));
		out.printVariable("sim_end", config.getParameter(StandardParameter.END, 0));
		out.newline();

		out.printComment("Time variable");
		out.printVariable("sim_time", "sim_start");
		out.newline();
	}

	@Override
	protected void printOpenFiles(CodeOutput out) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected void printSaveCurrentValues(CodeOutput out) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected void printCloseFiles(CodeOutput out) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected void printContainerCalculations(CodeOutput out) {
		int size = flowModel.getSimulationContainer().size();

		out.printComment("y = y + dy * t");
		for (int i = 1; i <= size; i++) {
			out.println("sim_ynew(" + i + ",:) = sim_y(" + i + ",:) + sim_k(" + i + ",:) * sim_ha(:,6);");
		}

		out.newline();
	}
}
