package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.MatlabAttachment;
import ch.zhaw.simulation.plugin.matlab.MatlabVisitor;
import ch.zhaw.simulation.plugin.StandardParameter;

/**
 * Code Generation for Runge-Kutta
 * 
 * <pre>
 * loop {
 * 		i := 0;
 * 		k1 := hf(x[i], y[i]);
 * 		k2 := hf(x[i] + h / 2, y[i] + k1 / 2);
 * 		k3 := hf(x[i] + h / 2, y[i] + k2 / 2);
 * 		k4 := hf(x[i] + h, y[i] + k3);
 * 		i := i + 1;
 * 		x[i] := x[i-1] + h;
 * 		y[i] := y[i-1] + (k1 + 2k2 + 2k3 + k4) / 6;
 * }
 * </pre>
 * 
 * @author Andreas Butti
 */
public class EulerCodeGenerator extends AbstractCodeGenerator {

	private static final String START = "sim_start";
	private static final String END = "sim_end";
	private static final String DT = "sim_dt";
	private static final String TIME = "sim_time";
	private static final String COUNT = "sim_count";
	
	private CodeOutput out;
	private SimulationFlowModel model;
	private MatlabVisitor visitor = new MatlabVisitor();
	private Vector <AbstractNamedSimulationData> datas = new Vector<AbstractNamedSimulationData>();

	@Override
	public void executeSimulation(SimulationDocument doc) throws IOException {
		extractBaseFile();

		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("only flow model supported currently");
		}

		this.model = doc.getFlowModel();
		SimulationConfiguration config = doc.getSimulationConfiguration();

		// Add container and parameter to a newly created vector
		// It will be used for file-handling
		datas.addAll(model.getSimulationContainer());
		datas.addAll(model.getSimulationParameter());

		String outputFile = getWorkingFolder() + File.separator + "simulation.m";
		this.out = new CodeOutput(new FileOutputStream(outputFile));

		out.printComment("Generated file by Simulation");

		out.newline();
		out.printComment("Cleanup");

		out.newline();

		out.println("clc; clear all; close all;");

		out.newline();

		out.printComment("Predefined constants");

		double start = config.getParameter(StandardParameter.START, 0);
		double end = config.getParameter(StandardParameter.END, 5);
		double dt = config.getParameter(StandardParameter.DT, 0.01);

		out.setVar(START, start);
		out.setVar(END, end);
		out.setVar(DT, dt);
		out.newline();
		out.printComment("Time variable");
		out.println(TIME + " = " + START + ";");

		outputContainerInitialisation();
		outputParameterInitialisation();

		out.newline();
		out.printComment("Open output files");

		outputOpenFiles(datas);

		out.newline();
		out.println(COUNT + " = ceil("+ END + " / " + DT + ");");
		out.newline();

		// TODO: DEBUG
		out.println("% Debug only!");
		out.println("x = zeros(1, " + COUNT + ");");

		out.newline();
		out.println("for i = 1 : sim_count + 1");
		out.indent();

		out.println(TIME + " = " + TIME + " + " + DT + ";");

		outputFlowCalculations();
		outputContainerCalculations();
		outputParameterCalculations();

		outputSaveCurrentValues(datas);

		out.detent();
		out.println("end");

		out.printComment("Close output files");
		outputCloseFiles(datas);

		// TODO: DEBUG
		out.println("% Debug only!");
		out.println("t = " + START + ":" + DT + ":" + END + ";");
		out.println("plot(t,x,'r','LineWidth',2);");
		out.println("legend('dt=" + dt + "',2);");
		out.println("title('Euler');");

		out.close();

		//Runtime.getRuntime().exec(new String[] { "gedit", outputFile });
	}

	private <T extends AbstractNamedSimulationData> void outputSaveCurrentValues(Vector<T> data) {
		out.newline();
		out.printComment("Save calculations");

		out.println("x(i)=Q.value;");

		for (T n : data) {
			String fp = n.getName() + ".fp";
			String value = n.getName() + ".value";
			this.out.println("fprintf(" + fp + ", '%e\\n', " + value + ");");
		}

	}

	private void outputContainerCalculations() {
		out.newline();
		out.printComment("Container calculations");

		for (SimulationContainerData c : model.getSimulationContainer()) {
			MatlabAttachment a = (MatlabAttachment) c.attachment;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				StringBuffer flows = new StringBuffer();
				for (FlowConnectorData f : model.getFlowConnectors()) {
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

				out.println(c.getName() + ".value = " + c.getName() + ".value + " +flows.toString() + " * " + DT + ";");
			}
		}
	}

	private void outputFlowCalculations() {
		out.newline();
		out.printComment("Flow calculations");

		for (FlowConnectorData c : model.getFlowConnectors()) {
			MatlabAttachment a = (MatlabAttachment) c.getValve().attachment;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				out.println(c.getValve().getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
			}
		}
	}

	private void outputParameterCalculations() {
		out.newline();
		out.printComment("Parameter calculations");

		Vector<SimulationParameterData> parameters = model.getSimulationParameter();
		sortByRelevanz(parameters);

		for (SimulationParameterData p : parameters) {
			MatlabAttachment a = (MatlabAttachment) p.attachment;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				out.println(p.getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
			}
		}
	}

	private void outputParameterInitialisation() {
		out.newline();
		out.printComment("Init parameter and flow values");

		Vector<SimulationParameterData> parameters = model.getSimulationParameter();
		sortByRelevanz(parameters);

		for (SimulationParameterData p : parameters) {
			MatlabAttachment a = (MatlabAttachment) p.attachment;

			if (a.isConst()) {
				out.println(p.getName() + ".value = " + a.getConstValue() + "; % constant");
			} else {
				try {
					out.println(p.getName() + ".value = " + a.getPreparedFormula(visitor) + ";");
				} catch (Exception e) {
					System.out.println("outputParameterInitialisation::" + p.getName());
					e.printStackTrace();
				}
			}
		}
	}

	private static <T extends AbstractNamedSimulationData> void sortByRelevanz(Vector<T> data) {
		Collections.sort(data, new Comparator<AbstractNamedSimulationData>() {

			@Override
			public int compare(AbstractNamedSimulationData o1, AbstractNamedSimulationData o2) {
				MatlabAttachment a = (MatlabAttachment) o1.attachment;
				MatlabAttachment b = (MatlabAttachment) o2.attachment;

				return a.getDependencyOrder() - b.getDependencyOrder();
			}

		});
	}

	private <T extends AbstractNamedSimulationData> void outputOpenFiles(Vector<T> datas) {
		for (T n : datas) {
			String var = n.getName() + ".fp";
			this.out.println(var + " = fopen('" + n.getName() + "_data.txt', 'w');");
		}
	}

	private <T extends AbstractNamedSimulationData> void outputCloseFiles(Vector<T> datas) {
		for (T n : datas) {
			String var = n.getName() + ".fp";
			this.out.println("fclose(" + var + ");");
		}
	}

	private void outputContainerInitialisation() {
		out.newline();
		out.printComment("Init container values");

		Vector<SimulationContainerData> containers = model.getSimulationContainer();
		sortByRelevanz(containers);

		for (SimulationContainerData c : containers) {
			MatlabAttachment a = (MatlabAttachment) c.attachment;

			if (a.isConst()) {
				out.println(c.getName() + ".value = " + a.getConstValue() + ";");
			} else {
				out.println(c.getName() + ".value = " + a.getPreparedFormula(visitor) + "; % constant");
			}
		}
	}
}
