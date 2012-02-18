package ch.zhaw.simulation.sim.mo.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.sim.StandardParameter;
import ch.zhaw.simulation.sim.mo.MOAttachment;
import ch.zhaw.simulation.sim.mo.MOVisitor;

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
public class RungeKuttaCodegen extends AbstractCodegen {
	private CodeOutput out;
	private SimulationDocument model;
	private MOVisitor visitor = new MOVisitor();

	public RungeKuttaCodegen() {
	}

	@Override
	public void crateSimulation(SimulationDocument model) throws IOException {
		extractBaseFile();

		this.model = model;
		SimulationConfiguration config = model.getSimulationConfiguration();

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

		for (SimulationContainer c : model.getSimulationContainer()) {
			MOAttachment a = (MOAttachment) c.a;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				StringBuffer flows = new StringBuffer();
				for (FlowConnector f : model.getFlowConnectors()) {
					if (f.getSource() == c) {
						flows.append("-");
						flows.append(f.getParameterPoint().getName() + ".value");
					}
					if (f.getTarget() == c) {
						if (flows.toString().length() != 0) {
							flows.append("+");
						}

						flows.append(f.getParameterPoint().getName() + ".value");
					}
				}

				out.println(c.getName() + ".value+=" + flows.toString() + "/_dt;");
			}
		}
	}

	private void outputFlowCalculations() {
		out.newline();
		out.printComment("Flow calculations");

		for (FlowConnector c : model.getFlowConnectors()) {
			MOAttachment a = (MOAttachment) c.getParameterPoint().a;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				out.println(c.getParameterPoint().getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
			}
		}
	}

	private void outputParameterCalculations() {
		out.newline();
		out.printComment("Parameter calculations");

		for (SimulationParameter p : model.getSimulationParameter()) {
			MOAttachment a = (MOAttachment) p.a;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				out.println(p.getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
			}
		}
	}

	private void outputParameterInitialisation() {
		out.newline();
		out.printComment("Init parameter and flow values");

		Vector<SimulationParameter> parameters = model.getSimulationParameter();

		Collections.sort(parameters, new Comparator<SimulationParameter>() {

			@Override
			public int compare(SimulationParameter o1, SimulationParameter o2) {
				MOAttachment a = (MOAttachment) o1.a;
				MOAttachment b = (MOAttachment) o2.a;

				return a.getDependencyOrder() - b.getDependencyOrder();
			}

		});

		for (SimulationParameter p : parameters) {
			MOAttachment a = (MOAttachment) p.a;

			if (a.isConst()) {
				out.println(p.getName() + ".value=" + a.getConstValue() + ";");
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

	private void outputContainerInitialisation() {
		out.newline();
		out.printComment("Init container values");

		for (SimulationContainer c : model.getSimulationContainer()) {
			MOAttachment a = (MOAttachment) c.a;

			if (a.isConst()) {
				out.println(c.getName() + ".value=" + a.getConstValue() + ";");
			} else {
				out.println(c.getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
			}
		}
	}
}
