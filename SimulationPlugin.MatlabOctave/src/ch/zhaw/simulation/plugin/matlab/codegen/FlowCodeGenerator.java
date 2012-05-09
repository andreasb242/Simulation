package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.matlab.FlowModelAttachment;
import ch.zhaw.simulation.plugin.matlab.MatlabVisitor;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 */
public abstract class FlowCodeGenerator extends AbstractCodeGenerator {

	protected static final String START = "sim_start";
	protected static final String END = "sim_end";
	protected static final String TIME = "sim_time";

	protected SimulationFlowModel flowModel;
	protected SimulationConfiguration config;
	protected MatlabVisitor visitor = new MatlabVisitor();

	protected Vector<AbstractNamedSimulationData> dataVector = new Vector<AbstractNamedSimulationData>();

	public FlowCodeGenerator() {

	}

	protected void initSimulation(SimulationDocument doc) throws IOException {
		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("only flow model supported currently");
		}

		flowModel = doc.getFlowModel();
		config = doc.getSimulationConfiguration();

		// Add container and parameter to a newly created vector
		// It will be used for file-handling
		dataVector.clear();
		dataVector.addAll(flowModel.getSimulationContainer());
		dataVector.addAll(flowModel.getSimulationParameter());
	}

	protected abstract void printPredefinedConstants(CodeOutput out);
	protected abstract void printContainerCalculations(CodeOutput out);

	protected void printHeader(CodeOutput out) {
		out.printComment("Generated file by Simulation");
		out.newline();

		out.printComment("Cleanup");
		out.println("clc; clear all; close all;");
		out.newline();
	}

	/**
	 * Print out container initialisation
	 *
	 * @param out file to write on
	 */
	protected void printContainerInitialisation(CodeOutput out) {
		Vector<SimulationContainerData> containers = flowModel.getSimulationContainer();
		sortByRelevanz(containers);

		out.printComment("Init container values");

		for (SimulationContainerData container : containers) {
			FlowModelAttachment attachment = (FlowModelAttachment) container.attachment;

			if (attachment.isConst()) {
				out.println(container.getName() + ".value = " + attachment.getConstValue() + "; % constant");
			} else {
				out.println(container.getName() + ".value = " + attachment.getPreparedFormula(visitor) + ";");
			}
		}

		out.newline();
	}

	/**
	 * Print out parameter initialisation
	 *
	 * @param out file to write on
	 */
	protected void printParameterInitialisation(CodeOutput out) {
		Vector<SimulationParameterData> parameters = flowModel.getSimulationParameter();
		sortByRelevanz(parameters);

		out.printComment("Init parameter values");

		for (SimulationParameterData parameter : parameters) {
			FlowModelAttachment attachment = (FlowModelAttachment) parameter.attachment;

			if (attachment.isConst()) {
				out.println(parameter.getName() + ".value = " + attachment.getConstValue() + "; % constant");
			} else {
				out.println(parameter.getName() + ".value = " + attachment.getPreparedFormula(visitor) + ";");
			}
		}
		out.newline();
	}

	protected void printFlowCalculations(CodeOutput out) {
		out.printComment("Flow calculations");

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			FlowModelAttachment a = (FlowModelAttachment) c.getValve().attachment;

			out.println(c.getValve().getName() + ".value = " + a.getPreparedFormula(visitor) + ";");
		}
		out.newline();
	}

	protected void printParameterCalculations(CodeOutput out) {
		out.printComment("Parameter calculations");

		Vector<SimulationParameterData> parameters = flowModel.getSimulationParameter();
		sortByRelevanz(parameters);

		for (SimulationParameterData p : parameters) {
			FlowModelAttachment a = (FlowModelAttachment) p.attachment;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				out.println(p.getName() + ".value = " + a.getPreparedFormula(visitor) + ";");
			}
		}
		out.newline();
	}

	/**
	 * Sort a vector by dependency order of the elements
	 *
	 * @param data vector to sort
	 * @param <T>
	 */
	protected static <T extends AbstractNamedSimulationData> void sortByRelevanz(Vector<T> data) {
		Collections.sort(data, new Comparator<AbstractNamedSimulationData>() {

			@Override
			public int compare(AbstractNamedSimulationData o1, AbstractNamedSimulationData o2) {
				FlowModelAttachment a = (FlowModelAttachment) o1.attachment;
				FlowModelAttachment b = (FlowModelAttachment) o2.attachment;

				return a.getDependencyOrder() - b.getDependencyOrder();
			}

		});
	}

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printOpenFiles(CodeOutput out) {
		out.printComment("Open output files");
		for (AbstractNamedSimulationData namedData : dataVector) {
			String var = namedData.getName() + ".fp";
			out.println(var + " = fopen('" + namedData.getName() + "_data.txt', 'w');");
		}

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			String var = c.getValve().getName() + ".fp";
			out.println(var + " = fopen('" + c.getValve().getName() + "_data.txt', 'w');");
		}
		out.newline();
	}

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printCloseFiles(CodeOutput out) {
		out.printComment("Close output files");
		for (AbstractNamedSimulationData namedData : dataVector) {
			String fp = namedData.getName() + ".fp";
			out.println("fclose(" + fp + ");");
		}

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			String fp = c.getValve().getName() + ".fp";
			out.println("fclose(" + fp + ");");
		}
		out.println("fclose(fopen('matlab_finish.txt', 'w'));");
		out.newline();
	}

	/**
	 * @see ch.zhaw.simulation.plugin.matlab.codegen.AbstractCodeGenerator
	 */
	protected void printValuesToFile(CodeOutput out) {
		out.printComment("Save calculations");

		for (AbstractNamedSimulationData namedData : dataVector) {
			String fp = namedData.getName() + ".fp";
			String value = namedData.getName() + ".value";
			out.println("fprintf(" + fp + ", '%f\\t%e\\n', " + TIME + ", " + value + ");");
		}

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			String fp = c.getValve().getName() + ".fp";
			String value = c.getValve().getName() + ".value";
			out.println("fprintf(" + fp + ", '%f\\t%e\\n', " + TIME + ", " + value + ");");
		}
		out.newline();
	}

	protected void printInitDebug(CodeOutput out) {
		Vector<AbstractNamedSimulationData> namedDatas = new Vector<AbstractNamedSimulationData>();

		namedDatas.addAll(dataVector);

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			namedDatas.add(c.getValve());
		}

		out.printComment("DEBUG");
		out.println("tmp_idx = 1;");
		out.println("tmp_y = zeros(" + (namedDatas.size() + 1) + ",1000);");
		out.newline();
	}

	protected void printDebug(CodeOutput out) {
		Vector<AbstractNamedSimulationData> namedDatas = new Vector<AbstractNamedSimulationData>();

		namedDatas.addAll(dataVector);

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			namedDatas.add(c.getValve());
		}

		out.printComment("DEBUG");
		out.println("tmp_y(1, tmp_idx) = sim_time;");
		for (int i = 1; i <= namedDatas.size(); i++) {
			//out.printComment(namedDatas.get(i-1).getName());
			out.println("tmp_y(" + (i + 1) + ", tmp_idx) = " + namedDatas.get(i-1).getName() + ".value;");
		}
		out.println("tmp_idx = tmp_idx + 1;");
		out.newline();
	}

	protected void printDebugGraph(CodeOutput out) {
		Vector<AbstractNamedSimulationData> namedDatas = new Vector<AbstractNamedSimulationData>();

		namedDatas.addAll(dataVector);

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			namedDatas.add(c.getValve());
		}

		out.printComment("DEBUG");
		out.println("tmp_res = tmp_y(:, 1:tmp_idx-1);");
		for (int i = 1; i <= namedDatas.size(); i++) {
			out.println("figure(" + i + ");");
			out.println("stem(tmp_res(1,:), tmp_res(" + (i + 1) + ",:));");
			out.println("title('" + namedDatas.get(i-1).getName() + "');");
		}
		out.newline();
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

	/**
	 * Print global variables. It prints only constant parameters.
	 *
	 * @param out
	 */
	protected void printGlobal(CodeOutput out) {
		StringBuilder builder = new StringBuilder();
		boolean isEmpty = true;

		builder.append("% Global constant parameters\n");
		builder.append("global");
		for (AbstractNamedSimulationData namedData : dataVector) {
			isEmpty = false;
			builder.append(" ");
			builder.append(namedData.getName());

		}

		for (FlowConnectorData c : flowModel.getFlowConnectors()) {
			isEmpty = false;
			builder.append(" ");
			builder.append(c.getValve().getName());
		}

		builder.append(";");

		if (!isEmpty) {
			out.println(builder.toString());
			out.newline();
		}
	}

	protected void printFlowToDifferential(CodeOutput out) {
		Vector<SimulationContainerData> containers = flowModel.getSimulationContainer();
		int size = containers.size();

		out.println("sim_dy = zeros(" + size + ", 1);");
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

			out.println("sim_dy(" + i + ",1) = " + flows.toString() + ";");
		}
	}

	protected void printVectorToContainer(CodeOutput out) {
		Vector<SimulationContainerData> containers = flowModel.getSimulationContainer();
		int size = containers.size();

		out.printComment("Convert vector to container");
		for (int i = 1; i <= size; i++) {
			SimulationContainerData container = containers.get(i - 1);
			out.println(container.getName() + ".value = sim_y(" + i + ");");
		}
		out.newline();
	}

	protected void printVectorToContainerFlow(CodeOutput out) {
		Vector<SimulationContainerData> containers = flowModel.getSimulationContainer();
		SimulationContainerData container;
		int i;

		int containerSize = containers.size();

		out.printComment("Convert vector to container/flow");
		for (i = 1; i <= containerSize; i++) {
			container = containers.get(i - 1);
			out.println(container.getName() + ".value = sim_y(" + i + ");");
		}

		out.newline();
	}

	protected void printTimeStep(CodeOutput out) {
		out.printComment("Increase time");
		out.printComment("t = t + dt");
		out.println("sim_time = sim_time + sim_dt;");
		out.newline();
	}
}
