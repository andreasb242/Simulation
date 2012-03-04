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
public class EulerCodegen extends AbstractCodegen {
	private CodeOutput out;
	private SimulationFlowModel model;
	private MatlabVisitor visitor = new MatlabVisitor();
	private Vector<String> openFiles = new Vector<String>();

	public EulerCodegen() {
	}

	@Override
	public void crateSimulation(SimulationDocument doc) throws IOException {
		extractBaseFile();

		openFiles.clear();

		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("only flow model supported currently");
		}

		this.model = doc.getFlowModel();
		SimulationConfiguration config = doc.getSimulationConfiguration();

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

		out.setVar("_start", start);
		out.setVar("_end", end);
		out.setVar("_dt", dt);
		out.newline();
		out.printComment("Time variable");
		out.println("_time = _start;");

		outputContainerInitialisation();
		outputParameterInitialisation();

		out.newline();
		out.printComment("Open output files");

		outputOpenFiles(model.getSimulationContainer());
		outputOpenFiles(model.getSimulationParameter());

		out.newline();
		out.println("_count = ceil(_end / _dt)");
		out.newline();
		// TODO: DEBUG
		out.println("x=[1:_count];");

		out.newline();
		out.println("for _i = 1 : _count + 1");
		out.indent();

		out.println("_time += _dt;");

		outputFlowCalculations();
		outputContainerCalculations();
		outputParameterCalculations();

		outputSaveCurrentValues();

		out.detent();
		out.println("end");

		out.printComment("Print result !!! TODO !!!");
		out.println("t=" + start + ":" + dt + ":" + end + ";");

		out.println("plot(t,x(2,:),'r','LineWidth',2);");
		out.println("legend('dt=" + dt + "',2);");
		out.println("title('Euler');");

		out.close();

		Runtime.getRuntime().exec(new String[] { "gedit", outputFile });
	}

	private void outputSaveCurrentValues() {
		out.newline();
		out.printComment("Save calculations !!! TODO !!!");

		out.println("x(_i)=v2.value;");

	}

	private void outputContainerCalculations() {
		out.newline();
		out.printComment("Container calculations");

		for (SimulationContainerData c : model.getSimulationContainer()) {
			MatlabAttachment a = (MatlabAttachment) c.a;

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

				out.println(c.getName() + ".value+=" + flows.toString() + "*_dt;");
			}
		}
	}

	private void outputFlowCalculations() {
		out.newline();
		out.printComment("Flow calculations");

		for (FlowConnectorData c : model.getFlowConnectors()) {
			MatlabAttachment a = (MatlabAttachment) c.getValve().a;

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
			MatlabAttachment a = (MatlabAttachment) p.a;

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
			MatlabAttachment a = (MatlabAttachment) p.a;

			if (a.isConst()) {
				out.println(p.getName() + ".value=" + a.getConstValue() + "; # constant");
			} else {
				try {
					out.println(p.getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
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
				MatlabAttachment a = (MatlabAttachment) o1.a;
				MatlabAttachment b = (MatlabAttachment) o2.a;

				return a.getDependencyOrder() - b.getDependencyOrder();
			}

		});
	}

	private <T extends AbstractNamedSimulationData> void outputOpenFiles(Vector<T> data) {
		for (T n : data) {
			String var = n.getName() + ".fp";
			this.out.println(var + "=fopen('" + n.getName() + "_data.txt', 'w');");
			openFiles.add(var);
		}
	}

	private void outputCloseFiles() {
		// TODO: Files schliessen: openFiles
	}

	private void outputContainerInitialisation() {
		out.newline();
		out.printComment("Init container values");

		Vector<SimulationContainerData> containers = model.getSimulationContainer();
		sortByRelevanz(containers);

		for (SimulationContainerData c : containers) {
			MatlabAttachment a = (MatlabAttachment) c.a;

			if (a.isConst()) {
				out.println(c.getName() + ".value=" + a.getConstValue() + ";");
			} else {
				out.println(c.getName() + ".value=" + a.getPreparedFormula(visitor) + "; # constant");
			}
		}
	}
}
