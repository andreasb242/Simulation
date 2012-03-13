package ch.zhaw.simulation.plugin.matlab.codegen;

import butti.javalibs.util.FileUtil;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 */
public abstract class DefaultCodeGenerator extends AbstractCodeGenerator {

	protected SimulationFlowModel flowModel;
	protected SimulationConfiguration config;
	protected MatlabVisitor visitor = new MatlabVisitor();

	public DefaultCodeGenerator() {

	}

	protected void initSimulation(SimulationDocument doc) throws IOException {
		if (doc.getType() != SimulationType.FLOW_SIMULATION) {
			throw new IllegalArgumentException("only flow model supported currently");
		}

		flowModel = doc.getFlowModel();
		config = doc.getSimulationConfiguration();
	}

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
			MatlabAttachment attachment = (MatlabAttachment) container.attachment;

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
			MatlabAttachment attachment = (MatlabAttachment) parameter.attachment;

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
			MatlabAttachment a = (MatlabAttachment) c.getValve().attachment;

			// Konstanten nicht neu berechnen
			if (!a.isConst()) {
				out.println(c.getValve().getName() + ".value = " + a.getPreparedFormula(visitor) + ";");
			}
		}
		out.newline();
	}

	protected void printParameterCalculations(CodeOutput out) {
		out.printComment("Parameter calculations");

		Vector<SimulationParameterData> parameters = flowModel.getSimulationParameter();
		sortByRelevanz(parameters);

		for (SimulationParameterData p : parameters) {
			MatlabAttachment a = (MatlabAttachment) p.attachment;

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
				MatlabAttachment a = (MatlabAttachment) o1.attachment;
				MatlabAttachment b = (MatlabAttachment) o2.attachment;

				return a.getDependencyOrder() - b.getDependencyOrder();
			}

		});
	}
}
