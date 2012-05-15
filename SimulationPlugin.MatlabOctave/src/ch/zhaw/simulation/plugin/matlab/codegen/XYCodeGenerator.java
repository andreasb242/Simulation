package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.plugin.matlab.FlowModelAttachment;
import ch.zhaw.simulation.plugin.matlab.MatlabVisitor;
import ch.zhaw.simulation.plugin.matlab.XYModelAttachment;

/**
 * @author: bachi
 */
public class XYCodeGenerator extends AbstractCodeGenerator {

	/*
	public class SubModelData {
		public String							mesoName;
		public String 							modelName;
		public Vector<SimulationContainerData>	containerList;
		public Vector<SimulationParameterData>	parameterList;
		public Vector<FlowConnectorData>		connectorList;
	}
	 */

	public static final String FILENAME = "xy_main";

	public XYCodeGenerator() {
		//
	}
	public String getGeneratedFile() {
		return FILENAME;
	}

	@Override
	protected void initSimulation(SimulationDocument doc) throws IOException {

	}

	public void generateSimulation(SimulationDocument doc) throws Exception {
		CodeOutput 							out;
		SimulationXYModel 					xyModel;
		SubModel 							submodel;
		Vector<String>						variableList;
		Map<String, String>				    flowFunctionMap;

		String								functionName;
		String								flowFunction;

		Vector<SimulationContainerData>		containerList;
		Vector<SimulationParameterData>		parameterList;
		Vector<FlowConnectorData>			connectorList;

		FlowModelAttachment 				attachment;
		MatlabVisitor 						visitor;
		MesoVisitor 						mesoVisitor;
		String 								prefix;
		String 								variable;

		double 								start;
		double 								end;
		double								dt;

		int									i;

		variableList = new Vector<String>();
		flowFunctionMap = new HashMap<String, String>();

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME + ".m"));

		out.println("clear all;");
		out.println("close all;");
		out.println("clc;");
		out.newline();
		
		xyModel = doc.getXyModel();

		start = doc.getSimulationConfiguration().getParameter(StandardParameter.START, StandardParameter.DEFAULT_START);
		end   = doc.getSimulationConfiguration().getParameter(StandardParameter.END, StandardParameter.DEFAULT_END);
		dt    = doc.getSimulationConfiguration().getParameter(StandardParameter.DT, StandardParameter.DEFAULT_DT);

		out.printComment("Predefined constants");

		out.printVariable("sim_start", start);
		out.printVariable("sim_end", end);
		out.printVariable("sim_time", start);
		out.printVariable("sim_dt", dt);
		out.newline();

		// xy-model dimension (width/height)
		out.printComment("xy-model dimension");
		out.println("xymodel.width = " + xyModel.getWidth() + ";");
		out.println("xymodel.height = " + xyModel.getHeight() + ";");
		out.newline();

		// zero point
		out.printComment("xy-model zero displacement");
		out.println("xymodel.zero.x = " + xyModel.getZero().x + ";");
		out.println("xymodel.zero.y = " +xyModel.getZero().y + ";");
		out.newline();

		/*** generate numeric densities ***/
		out.printComment("init densities");
		out.println("for x = 1:xymodel.width");
		out.indent();
		out.println("for y = 1:xymodel.height");
		out.indent();
		out.println("tmp_x = x - xymodel.zero.x - 1;");
		out.println("tmp_y = y - xymodel.zero.y - 1;");

		// iterate over all densities in the model for every x and y
		for (DensityData density : xyModel.getDensity()) {

			// generate density function (formula to matrix transformation) as a new file
			functionName = generateDensityFunctionFile(density);

			// assign function(model-x, model-y) to matrix(matlab-x, matlab-y)
			out.println(density.getName() + ".matrix(y, x) = " + functionName + "(tmp_y, tmp_x);");
		}
		out.detent();
		out.println("end;");
		out.detent();
		out.println("end;");
		out.println("clear x y tmp_x tmp_y;");
		out.newline();

		// get gradient and laplace
		for (DensityData density : xyModel.getDensity()) {
			// gradient
			out.println("[ " + density.getName() + ".grad.dx " + ", " + density.getName() + ".grad.dy ] = gradient(" + density.getName() + ".matrix);");
			// laplace
			//out.println(density.getName() + ".laplace = del2(" + density.getName() + ".matrix);");
		}
		out.newline();

		/*** initialize meso compartments ***/
		out.printComment("init meso compartments");

		// iterate over meso-comp.
		for (MesoData  meso : xyModel.getMeso()) {
			prefix = meso.getName() + ".submodel.";
			visitor = new MatlabVisitor(prefix);
			out.printComment("init " + meso.getName());

			// set x/y
			out.println(meso.getName() + ".position.exact.x.value = " + meso.getXCenter() + ";");
			out.println(meso.getName() + ".position.exact.y.value = " + meso.getYCenter() + ";");
			out.println(meso.getName() + ".position.approx.x.value = uint32(" + meso.getName() + ".position.exact.x.value);");
			out.println(meso.getName() + ".position.approx.y.value = uint32(" + meso.getName() + ".position.exact.y.value);");
			variableList.add(meso.getName() + ".position.exact.x");
			variableList.add(meso.getName() + ".position.exact.y");
			variableList.add(meso.getName() + ".position.approx.x");
			variableList.add(meso.getName() + ".position.approx.y");

			/*** init submodel ***/
			submodel = meso.getSubmodel();
			generateFlowFunction(submodel, flowFunctionMap);

			// 1) add container
			containerList = submodel.getModel().getSimulationContainer();
			sortByRelevanz(containerList);
			for (SimulationContainerData container : containerList) {
				attachment = (FlowModelAttachment) container.attachment;
				variable = prefix + container.getName();
				out.println(variable + ".value = " + attachment.getPreparedFormula(visitor) + ";");
				variableList.add(variable);
			}

			// 2) add parameter
			parameterList = submodel.getModel().getSimulationParameter();
			sortByRelevanz(parameterList);
			for (SimulationParameterData parameter : parameterList) {
				attachment = (FlowModelAttachment) parameter.attachment;
				variable = prefix + parameter.getName();
				if (attachment.isConst()) {
					out.println(variable + ".value = " + attachment.getConstValue() + "; % constant");
				} else {
					out.println(variable + ".value = " + attachment.getPreparedFormula(visitor) + ";");
				}
				variableList.add(variable);
			}

			// 3) add connector
			connectorList = submodel.getModel().getFlowConnectors();
			for (FlowConnectorData connector : connectorList) {
				attachment = (FlowModelAttachment) connector.getValve().attachment;
				variable = prefix + connector.getValve().getName();
				out.println(variable + ".value = " + attachment.getPreparedFormula(visitor) + ";");
				variableList.add(variable);
			}

			// add density
			/*
			Iterator<SimulationDensityContainerData> densityContainerIter = submodel.getModel().getSimulationDensityContainer().iterator();
			while (densityContainerIter.hasNext()) {
				SimulationDensityContainerData densityContainer = densityContainerIter.next();
				DensityData density = densityContainer.getDensity();
				variable = meso.getName() + ".submodel." + densityContainer.getName() + "." + density.getName();
				out.println(variable + ".value = " + density.getFormula() + ";");
				variableList.add(variable);
			}
			*/
			printInitialValueVector(out, meso, prefix);
			out.newline();
		}

		printButcherTableau(out);

		fileOpen(out, variableList);
		fileWrite(out, variableList);

		out.println("sim_count = ceil(sim_end / sim_dt);");
		out.newline();
		out.println("for i = 1:sim_count");
		out.indent();

		/*** for every meso: calculate meso.dy and add to meso.y ***/
		i = 0;
		for (MesoData meso : xyModel.getMeso()) {
			flowFunction = flowFunctionMap.get(meso.getSubmodel().getName());
			if (flowFunction != null && meso.getSubmodel().getModel().getSimulationContainer().size() > 0) {
				prefix = meso.getName();
				printContainerCalculations(out, meso.getName(), flowFunction);
				printVectorToContainerFlow(out, prefix, meso.getSubmodel());
			}
			out.newline();

			mesoVisitor = new MesoVisitor(meso, xyModel.getDensity());

			// move meso
			out.printComment("move meso");
			if (meso.getDerivative() == MesoData.Derivative.FIRST_DERIVATIVE) {
				out.printComment("FIRST_DERIVATIVE");
				out.println(meso.getName() + ".position.exact.x.value = " + meso.getName() + ".position.exact.x.value + (" + ((XYModelAttachment) meso.attachment).getDataXFormula(mesoVisitor) + ") * sim_dt;");
				out.println(meso.getName() + ".position.exact.y.value = " + meso.getName() + ".position.exact.y.value + (" + ((XYModelAttachment) meso.attachment).getDataYFormula(mesoVisitor) + ") * sim_dt;");
				out.println(meso.getName() + ".position.approx.x.value = uint32(" + meso.getName() + ".position.exact.x.value);");
				out.println(meso.getName() + ".position.approx.y.value = uint32(" + meso.getName() + ".position.exact.y.value);");
				out.println("if " + meso.getName() + ".position.approx.x.value < 1");
				out.indent();
				out.println(meso.getName() + ".position.exact.x.value = 1;");
				out.println(meso.getName() + ".position.approx.x.value = 1;");
				out.detent();
				out.println("end;");
				out.println("if " + meso.getName() + ".position.approx.x.value > xymodel.width");
				out.indent();
				out.println(meso.getName() + ".position.exact.x.value = xymodel.width;");
				out.println(meso.getName() + ".position.approx.x.value = xymodel.width;");
				out.detent();
				out.println("end;");
				out.println("if " + meso.getName() + ".position.approx.y.value < 1");
				out.indent();
				out.println(meso.getName() + ".position.exact.y.value = 1;");
				out.println(meso.getName() + ".position.approx.y.value = 1;");
				out.detent();
				out.println("end;");
				out.println("if " + meso.getName() + ".position.approx.y.value > xymodel.height");
				out.indent();
				out.println(meso.getName() + ".position.exact.y.value = xymodel.height;");
				out.println(meso.getName() + ".position.approx.y.value = xymodel.height;");
				out.detent();
				out.println("end;");
			} else if (meso.getDerivative() == MesoData.Derivative.SECOND_DERIVATIVE) {
				out.printComment("SECOND_DERIVATIVE");
			} else {
				out.printComment("NO_DERIVATIVE");
			}
			out.newline();
		}

		fileWrite(out, variableList);

		out.printComment("t = t + dt");
		out.println("sim_time = sim_time + sim_dt;");
		out.newline();

		out.detent();
		out.println("end;");
		out.newline();

		fileClose(out, variableList);

		out.println("fclose(fopen('matlab_finish.txt', 'w'));");
		out.println("exit;");


		out.close();

	}

	private void fileOpen(CodeOutput out, Vector<String> variableList) {
		String filename;
		String fp;

		out.printComment("Open output files");
		for (String variable : variableList) {
			filename = "data_" + variable + ".txt";
			fp       = variable + ".fp";
			out.println(fp + " = fopen('" + filename + "', 'w');");
		}
		out.newline();
	}

	private void fileWrite(CodeOutput out, Vector<String> variableList) {
		String fp;
		String value;

		out.printComment("Save calculations");
		for (String variable : variableList) {
			fp    = variable + ".fp";
			value = variable + ".value";
			out.println("fprintf(" + fp + ", '%f\\t%e\\n', sim_time, " + value + ");");
		}
		out.newline();
	}

	private void fileClose(CodeOutput out, Vector<String> variableList) {
		String fp;

		out.printComment("Close output files");
		for (String variable : variableList) {
			fp = variable + ".fp";
			out.println("fclose(" + fp + ");");
		}
		out.newline();
	}

	private String generateDensityFunctionFile(DensityData density) throws Exception {
		String functionName;
		CodeOutput out;

		functionName = "xy_" + density.getName() + "_function";
		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + functionName + ".m"));

		out.println("function result = " + functionName + "(y, x)");
		out.indent();
		out.println("result = " + density.getFormula() + ";");
		out.detent();
		out.println("end");
		
		out.close();

		return functionName;
	}

	private void generateFlowFunction(SubModel submodel, Map<String, String> flowFunctionMap) throws Exception {
		String functionName;
		CodeOutput out;
		MatlabVisitor visitor;

		visitor = new MatlabVisitor(submodel.getName() + ".");

		functionName = "xy_" + submodel.getName() +"_dy";
		if (flowFunctionMap.containsValue(functionName) == false) {
			out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + functionName + ".m"));

			out.printComment("Flow calculation");
			out.println("function [ sim_dy " + submodel.getName() + " ] = " + functionName + "(sim_time, sim_y, " + submodel.getName() + ")");
			out.indent();
			printVectorToContainer(out, submodel);
			printParameterCalculations(out, submodel, visitor);
			printFlowCalculations(out, submodel, visitor);
			printFlowToDifferential(out, submodel);
			out.detent();
			out.println("end");

			out.close();

			flowFunctionMap.put(submodel.getName(), functionName);
		}
	}

	/**
	 * Print initial value vector. It prints only containers.
	 *
	 * @param out
	 */
	protected void printInitialValueVector(CodeOutput out, MesoData meso, String prefix) {
		StringBuilder builder;
		Vector<SimulationContainerData> containerList;
		boolean isEmpty = true;

		builder = new StringBuilder();
		containerList = meso.getSubmodel().getModel().getSimulationContainer();

		builder.append("% Initial value vector\n");
		builder.append(meso.getName() + ".y = [");
		for (SimulationContainerData container : containerList) {
			// to separate between vector elements
			// first element has no prefix
			if (!isEmpty) {
				builder.append(";");
			}

			isEmpty = false;
			builder.append(" ");
			builder.append(prefix + container.getName() + ".value");
		}
		builder.append(" ];");

		if (!isEmpty) {
			out.println(builder.toString());
		}
	}

	private void printVectorToContainer(CodeOutput out, SubModel submodel) {
		Vector<SimulationContainerData> containerList;
		int size;

		containerList  = submodel.getModel().getSimulationContainer();
		size = containerList.size();
		sortByRelevanz(containerList);

		out.printComment("Convert vector to container");
		for (int i = 1; i <= size; i++) {
			SimulationContainerData container = containerList.get(i - 1);
			out.println(submodel.getName() + "." + container.getName() + ".value = sim_y(" + i + ");");
		}
		out.newline();
	}

	private void printParameterCalculations(CodeOutput out, SubModel submodel, MatlabVisitor visitor) {
		Vector<SimulationParameterData> parameterList;
		FlowModelAttachment attachment;

		parameterList = submodel.getModel().getSimulationParameter();
		sortByRelevanz(parameterList);

		out.printComment("Parameter calculations");
		for (SimulationParameterData parameter : parameterList) {
			attachment = (FlowModelAttachment) parameter.attachment;

			// Konstanten nicht neu berechnen
			if (!attachment.isConst()) {
				out.println(submodel.getName() + "." + parameter.getName() + ".value = " + attachment.getPreparedFormula(visitor) + ";");
			}
		}
		out.newline();
	}

	private void printFlowCalculations(CodeOutput out, SubModel submodel, MatlabVisitor visitor) {
		Vector<FlowConnectorData> connectorList;
		FlowModelAttachment attachment;

		connectorList = submodel.getModel().getFlowConnectors();

		out.printComment("Flow calculations");
		for (FlowConnectorData connector : connectorList) {
			attachment = (FlowModelAttachment) connector.getValve().attachment;

			out.println(submodel.getName() + "." + connector.getValve().getName() + ".value = " + attachment.getPreparedFormula(visitor) + ";");
		}
		out.newline();
	}

	private void printFlowToDifferential(CodeOutput out, SubModel submodel) {
		Vector<SimulationContainerData> containerList;
		SimulationContainerData container;
		StringBuffer flows;
		int size;

		containerList = submodel.getModel().getSimulationContainer();
		size = containerList.size();

		out.printComment("Convert flow to vector");
		out.println("sim_dy = zeros(" + size + ", 1);");
		for (int i = 1; i <= size; i++) {
			container = containerList.get(i - 1);

			flows = new StringBuffer();

			for (FlowConnectorData flow : submodel.getModel().getFlowConnectors()) {
				if (flow.getSource() == container) {
					flows.append("-");
					flows.append(submodel.getName() + "." + flow.getValve().getName() + ".value");
				}
				if (flow.getTarget() == container) {
					if (flows.toString().length() != 0) {
						flows.append("+");
					}

					flows.append(submodel.getName() + "." + flow.getValve().getName() + ".value");
				}
			}

			out.println("sim_dy(" + i + ",1) = " + flows.toString() + ";");
		}
	}

	private void printButcherTableau(CodeOutput out) {
		out.printComment("Bucher tableau");
		out.println("sim_a = [ 0   1/2     0   0");
		out.println("          0     0   1/2   0");
		out.println("          0     0     0   1");
		out.println("          0     0     0   0 ];");
		out.newline();

		out.println("sim_b = [ 1/6; 1/3; 1/3; 1/6 ];");
		out.newline();

		out.println("sim_c = [ 0 1/2 1/2 1 ];");
		out.newline();
	}

	private void printContainerCalculations(CodeOutput out, String mesoName, String flowFunction) {

		out.printComment("Reset intermediate steps");
		out.println(mesoName + ".k = zeros(length(" + mesoName + ".y), 4);");
		out.println("[ " + mesoName + ".k(:,1) " + mesoName + ".submodel ] = " + flowFunction + "(sim_time + sim_dt * sim_c(1), " + mesoName + ".y + sim_dt * " + mesoName + ".k * sim_a(:,1)," + mesoName + ".submodel);");
		out.println("[ " + mesoName + ".k(:,2) " + mesoName + ".submodel ] = " + flowFunction + "(sim_time + sim_dt * sim_c(2), " + mesoName + ".y + sim_dt * " + mesoName + ".k * sim_a(:,2)," + mesoName + ".submodel);");
		out.println("[ " + mesoName + ".k(:,3) " + mesoName + ".submodel ] = " + flowFunction + "(sim_time + sim_dt * sim_c(3), " + mesoName + ".y + sim_dt * " + mesoName + ".k * sim_a(:,3)," + mesoName + ".submodel);");
		out.println("[ " + mesoName + ".k(:,4) " + mesoName + ".submodel ] = " + flowFunction + "(sim_time + sim_dt * sim_c(4), " + mesoName + ".y + sim_dt * " + mesoName + ".k * sim_a(:,4)," + mesoName + ".submodel);");
		out.newline();

		out.printComment("dy");
		out.println(mesoName + ".dy = " + mesoName + ".k * sim_b;");
		out.newline();

		out.printComment("y = y + dt * dy");
		out.println(mesoName + ".y = " + mesoName + ".y + sim_dt * " + mesoName + ".dy;");
		out.newline();

	}

	private void printVectorToContainerFlow(CodeOutput out, String prefix, SubModel submodel) {
		Vector<SimulationContainerData> containerList;
		SimulationContainerData container;
		int containerSize;
		int i;

		containerList = submodel.getModel().getSimulationContainer();

		containerSize = containerList.size();

		sortByRelevanz(containerList);

		out.printComment("Convert vector to container/flow");
		for (i = 1; i <= containerSize; i++) {
			container = containerList.get(i - 1);
			out.println(prefix + "." + container.getName() + ".value = " + prefix + ".y(" + i + ");");
		}
		out.newline();
	}

	/**
	 * Sort a vector by dependency order of the elements
	 *
	 * @param data vector to sort
	 * @param <T>
	 */
	private static <T extends AbstractNamedSimulationData> void sortByRelevanz(Vector<T> data) {
		Collections.sort(data, new Comparator<AbstractNamedSimulationData>() {

			@Override
			public int compare(AbstractNamedSimulationData o1, AbstractNamedSimulationData o2) {
				FlowModelAttachment a = (FlowModelAttachment) o1.attachment;
				FlowModelAttachment b = (FlowModelAttachment) o2.attachment;

				return a.getDependencyOrder() - b.getDependencyOrder();
			}

		});
	}
}
