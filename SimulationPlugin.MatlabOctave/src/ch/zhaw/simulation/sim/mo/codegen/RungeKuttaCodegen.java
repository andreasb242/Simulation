package ch.zhaw.simulation.sim.mo.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationParameter;
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
		out.setVar("_start", config.getParameter(StandardParameter.START, 0));
		out.setVar("_end", config.getParameter(StandardParameter.END, 5));
		out.setVar("_dt", config.getParameter(StandardParameter.DT, 0.01));
		out.newline();
		out.printComment("Time variable");
		out.println("_time = _start;");

		outputContainerInitialisation();
		outputParameterInitialisation();

		out.newline();
		out.println("for _i = 1 : ceil(_end / _dt)");
		out.indent();

		out.println("_time += _dt;");

		outputParameterCalculations();

		out.detent();
		out.println("end");

		// out.printComment("Print result");
		// out.println("t=0:eps:b;");
		//
		// out.println("plot(t,x(2,:),'r','LineWidth',2);");
		// out.println("legend('eps=0.01',2);");
		// out.println("title('Klassisches Runge-Kutta-Verfahren');");

		out.close();

		Runtime.getRuntime().exec(new String[] { "gedit", outputFile });
	}

	private void outputParameterCalculations() {
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
		out.printComment("Init parameter values");

		for (SimulationParameter p : model.getSimulationParameter()) {
			MOAttachment a = (MOAttachment) p.a;

			if (a.isConst()) {
				out.println(p.getName() + ".value=" + a.getConstValue() + ";");
				out.println(p.getName() + ".isConst=1;");
			} else {
				try {
				out.println(p.getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
				} catch(Exception e) {
					System.out.println("outputParameterInitialisation::"+p.getName());
					e.printStackTrace();
				}
				out.println(p.getName() + ".isConst=1;");
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
				out.println(c.getName() + ".isConst=1;");
			} else {
				out.println(c.getName() + ".value=" + a.getPreparedFormula(visitor) + ";");
				out.println(c.getName() + ".isConst=1;");
			}
		}
	}
}
