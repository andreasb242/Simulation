package ch.zhaw.simulation.sim.mo.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.sim.StandardParameter;

/**
 * Code Generation for Runge-Kutta
 * 
 * @author Andreas Butti
 */
public class RungeKuttaCodegen extends AbstractCodegen {
	public RungeKuttaCodegen() {
		this.predefinedFiles = new String[] { "rungekutta_f.m" };
	}

	@Override
	public void crateSimulation(SimulationDocument model) throws IOException {
		extractBaseFile();
		
		SimulationConfiguration config = model.getSimulationConfiguration();

		CodeOutput out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + "simulation.m"));
		
		out.printComment("Generated file by Simulation");
		
		out.newline();
		out.printComment("Cleanup");
		
		out.newline();
		
		out.println("clc; clear all; close all;");

		out.newline();
		
		out.setVar("a", 0);
		out.setVar("b", 10);
		out.setVar("x0", config.getParameter(StandardParameter.START, 0));
		out.setVar("x1", config.getParameter(StandardParameter.END, 5));
		out.setVar("eps", config.getParameter(StandardParameter.DT, 0.01));
		
		out.println("x=[x0; x1];");
		out.println("for i=1:ceil(b/eps)");
		
		out.indent();

		out.println("k(1:2,1)=rungekutta_f(x(1:2,i));");
		out.println("k(1:2,2)=rungekutta_f(x(1:2,i)+eps*0.5*k(1:2,1));");
		out.println("k(1:2,3)=rungekutta_f(x(1:2,i)+eps*0.5*k(1:2,2));");
		out.println("k(1:2,4)=rungekutta_f(x(1:2,i)+eps*k(1:2,3));");
		out.println("x(1:2,i+1)=x(1:2,i)+eps*((1/6)*k(1:2,1)+(1/3)*k(1:2,2)+(1/3)*k(1:2,3)+(1/6)*k(1:2,4));");
		
		out.detent();

		out.println("end");
		out.println("t=0:eps:b;");

		out.println("plot(t,x(2,:),'r','LineWidth',2);");
		out.println("legend('eps=0.01',2);");
		out.println("title('Klassisches Runge-Kutta-Verfahren');");
		out.close();
	}
}
