package ch.zhaw.simulation.plugin.matlab.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
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
		double                              framesPerUnit;
		double								diffusionCoefficient;

		variableList = new Vector<String>();
		flowFunctionMap = new HashMap<String, String>();

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME + ".m"));

		generateGradientFunctionFile();
		generateDiffusionFunctionFile();
		generateDensityOutputFunctionFile();

		out.println("clear all;");
		out.println("close all;");
		out.println("clc;");
		out.newline();
		
		xyModel = doc.getXyModel();

		start = doc.getSimulationConfiguration().getParameter(StandardParameter.START, StandardParameter.DEFAULT_START);
		end   = doc.getSimulationConfiguration().getParameter(StandardParameter.END, StandardParameter.DEFAULT_END);
		dt    = doc.getSimulationConfiguration().getParameter(StandardParameter.DT, StandardParameter.DEFAULT_DT);
		framesPerUnit = doc.getSimulationConfiguration().getParameter(StandardParameter.FRAMES, StandardParameter.DEFAULT_FRAMES);
		diffusionCoefficient = doc.getSimulationConfiguration().getParameter(StandardParameter.DIFFUSION, StandardParameter.DEFAULT_DIFFUSION);

		out.printComment("Predefined constants");
		out.printVariable("sim_start", start);
		out.printVariable("sim_end", end);
		out.printVariable("sim_time", start);
		out.printVariable("sim_dt", dt);
		out.newline();

		out.printComment("Frames per unit");
		out.println("sim_frame_per_unit = 1/" + framesPerUnit + ";");
		out.println("sim_frame_count = sim_frame_per_unit;");
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
			out.println("density." + density.getName() + ".matrix(y, x) = " + functionName + "(tmp_y, tmp_x);");
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
			out.println("[ density." + density.getName() + ".grad.dx " + ", density." + density.getName() + ".grad.dy ] = gradient(density." + density.getName() + ".matrix);");
			// laplace
			//out.println(density.getName() + ".laplace = del2(" + density.getName() + ".matrix);");
		}
		out.newline();

		/*** initialize meso compartments ***/
		out.printComment("init meso compartments");

		// iterate over meso-comp.
		for (MesoData  meso : xyModel.getMeso()) {
			prefix = meso.getName() + ".submodel.";
			visitor = new MatlabVisitor(prefix, meso.getSubmodel());
			out.printComment("init " + meso.getName());

			// set x/y
			out.println(meso.getName() + ".position.exact.x.value = " + meso.getXCenter() + ";");
			out.println(meso.getName() + ".position.exact.y.value = " + meso.getYCenter() + ";");
			out.println(meso.getName() + ".position.approx.x.value = uint32(" + meso.getName() + ".position.exact.x.value);");
			out.println(meso.getName() + ".position.approx.y.value = uint32(" + meso.getName() + ".position.exact.y.value);");
			out.println("position = " + meso.getName() + ".position.approx;");

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

			printInitialValueVector(out, meso, prefix);
			out.newline();
		}

		printButcherTableau(out);

		fileOpen(out, variableList);
		fileWrite(out, variableList);

		out.println("sim_count = ceil(sim_end / sim_dt) - ceil(sim_start / sim_dt);");
		out.println("sim_progress_new = uint8(0);");
		out.println("sim_progress_old = uint8(0);");
		out.newline();
		out.println("for i = sim_start:sim_count");
		out.indent();

		/*** progress ***/
		out.printComment("progress bar");
		out.println("sim_progress_new = uint8(100*sim_time/sim_end);");
		out.println("if sim_progress_new > sim_progress_old");
		out.indent();
		out.println("fclose(fopen(strcat('progress_', int2str(sim_progress_new)), 'w'));");
		out.println("sim_progress_old = sim_progress_new;");
		out.detent();
		out.println("end;");

		/*** container: for every meso, calculate meso.dy and add to meso.y ***/
		for (MesoData meso : xyModel.getMeso()) {
			flowFunction = flowFunctionMap.get(meso.getSubmodel().getName());
			if (flowFunction != null) {
				prefix = meso.getName();
				printContainerCalculations(out, meso, flowFunction);
				printMainVectorToContainer(out, prefix, meso.getSubmodel());

			}
			out.newline();
		}

		// move meso
		for (MesoData meso : xyModel.getMeso()) {
			mesoVisitor = new MesoVisitor(meso, xyModel.getDensity());

			out.printComment("move meso");
			if (meso.getDerivative() == MesoData.Derivative.FIRST_DERIVATIVE) {
				out.printComment("FIRST_DERIVATIVE");
				out.println(meso.getName() + ".position.exact.x.value = " + meso.getName() + ".position.exact.x.value + (" + ((XYModelAttachment) meso.attachment).getDataXFormula(mesoVisitor) + ") * sim_dt;");
				out.println(meso.getName() + ".position.exact.y.value = " + meso.getName() + ".position.exact.y.value + (" + ((XYModelAttachment) meso.attachment).getDataYFormula(mesoVisitor) + ") * sim_dt;");
				out.println(meso.getName() + ".position.approx.x.value = uint32(" + meso.getName() + ".position.exact.x.value);");
				out.println(meso.getName() + ".position.approx.y.value = uint32(" + meso.getName() + ".position.exact.y.value);");
			} else if (meso.getDerivative() == MesoData.Derivative.SECOND_DERIVATIVE) {
				// TODO: Second Derivative
				out.printComment("SECOND_DERIVATIVE: TODO!");
			} else {
				out.printComment("NO_DERIVATIVE");
			}
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
			out.newline();
		}

		out.printComment("t = t + dt");
		out.println("sim_time = sim_time + sim_dt;");
		out.newline();

		out.println("if (sim_frame_count <= sim_time || i == sim_count)	");
		out.indent();
		fileWrite(out, variableList);
		out.println("sim_frame_count = sim_frame_count + sim_frame_per_unit;");
		out.detent();
		out.println("end;");
		out.newline();

		printDiffusionCalculation(out, xyModel, diffusionCoefficient);
		printGradientCalculation(out, xyModel);

		out.detent();
		out.println("end;");
		out.newline();

		fileClose(out, variableList);

		for (DensityData density : xyModel.getDensity()) {
			out.println("sim_density_out('data_density." + density.getName() + ".txt', density." + density.getName() + ".matrix);");
		}

		out.println("fclose(fopen('matlab_finish.txt', 'w'));");
		out.println("exit;");


		out.close();

	}

	private void generateGradientFunctionFile() throws FileNotFoundException {
		CodeOutput out;
		String content =
				"function [dx dy] = sim_gradient(f)\n" +
				"    [ylen xlen] = size(f);\n" +
				"    dx = zeros(ylen, xlen);\n" +
				"    dy = zeros(ylen, xlen);\n" +
				"    for y = 1:ylen\n" +
				"        for x = 1:xlen\n" +
				"            if x == xlen || y == ylen\n" +
				"                dx(y,x) = 0;\n" +
				"                dy(y,x) = 0;\n" +
				"            else\n" +
				"                dx(y,x) = f(y, x + 1) - f(y, x);\n" +
				"                dy(y,x) = f(y + 1, x) - f(y, x);\n" +
				"            end;\n" +
				"        end;\n" +
				"    end;\n" +
				"end";

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + "sim_gradient.m"));
		out.println(content);
		out.close();
	}

	private void generateDiffusionFunctionFile() throws FileNotFoundException {
		CodeOutput out;
		String content =
				"function [g] = sim_diffusion(f, dt, a)\n" +
				"    [ylen xlen] = size(f);\n" +
				"    df = zeros(ylen, xlen);\n" +
				"    for y = 1:ylen\n" +
				"        for x = 1:xlen\n" +
				"            if x == 1 && y == 1\n" +
				"                df(y, x) = dt * a * (f(y+1, x) + f(y, x+1) - 2*f(y,x));\n" +
				"            elseif x == xlen && y == 1\n" +
				"                df(y, x) = dt * a * (f(y+1, x) + f(y, x-1) - 2*f(y,x));\n" +
				"            elseif x == 1 && y == ylen\n" +
				"                df(y, x) = dt * a * (f(y-1, x) + f(y, x+1) - 2*f(y,x));\n" +
				"            elseif x == xlen && y == ylen\n" +
				"                df(y, x) = dt * a * (f(y-1, x) + f(y, x-1) - 2*f(y,x));\n" +
				"            elseif x == 1\n" +
				"                df(y, x) = dt * a * (f(y+1, x) + f(y-1, x) + f(y, x+1) - 3*f(y,x));\n" +
				"            elseif x == xlen\n" +
				"                df(y, x) = dt * a * (f(y+1, x) + f(y-1, x) + f(y, x-1) - 3*f(y,x));\n" +
				"            elseif y == 1\n" +
				"                df(y, x) = dt * a * (f(y+1, x) + f(y, x+1) + f(y, x-1) - 3*f(y,x));\n" +
				"            elseif y == ylen\n" +
				"                df(y, x) = dt * a * (f(y-1, x) + f(y, x+1) + f(y, x-1) - 3*f(y,x));\n" +
				"            else\n" +
				"                df(y, x) = dt * a * (f(y+1, x) + f(y-1, x) + f(y, x+1) + f(y, x-1) - 4*f(y,x));\n" +
				"            end;\n" +
				"        end;\n" +
				"    end;\n" +
				"    g = f + df;\n" +
				"end";

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + "sim_diffusion.m"));
		out.println(content);
		out.close();
	}

	private void generateDensityOutputFunctionFile() throws FileNotFoundException {
		CodeOutput out;
		String content =
				"function tmp = sim_density_out(filename, density)\n" +
						"    [ylen xlen] = size(density);\n" +
						"    df = zeros(ylen, xlen);\n" +
						"    fp = fopen(filename, 'w');\n" +
						"    fprintf(fp, '%d %d\\n', xlen, ylen);\n" +
						"    for y = 1:ylen\n" +
						"        for x = 1:xlen\n" +
						"           fprintf(fp, '%f ', density(y,x));\n" +
						"        end;\n" +
						"        fprintf(fp, '\\n');" +
						"    end;\n" +
						"    fclose(fp);\n" +
						"end";

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + "sim_density_out.m"));
		out.println(content);
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

		visitor = new MatlabVisitor(submodel.getName() + ".", submodel);

		functionName = "xy_" + submodel.getName() +"_dy";
		if (flowFunctionMap.containsValue(functionName) == false) {
			out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + functionName + ".m"));

			out.printComment("Flow calculation");
			out.println("function [ sim_dy " + submodel.getName() + " density ] = " + functionName + "(sim_time, sim_y, " + submodel.getName() + ", position, density)");
			out.indent();
			printFlowFunctionVectorToContainer(out, submodel);
			printParameterCalculations(out, submodel, visitor);
			printFlowCalculations(out, submodel, visitor);
			printFlowDensityCalculation(out, submodel);
			printFlowToDifferential(out, submodel);
			out.detent();
			out.println("end");

			out.close();

			flowFunctionMap.put(submodel.getName(), functionName);
		}
	}

	private void printFlowDensityCalculation(CodeOutput out, SubModel submodel) {
		Vector<FlowConnectorData> connectorList;
		SimulationDensityContainerData densityContainer;
		String densityStr;
		String flowStr;

		connectorList = submodel.getModel().getFlowConnectors();

		out.printComment("Density calculations");
		for (FlowConnectorData connector : connectorList) {
			// consume
			if (connector.getSource() instanceof SimulationDensityContainerData) {
				densityContainer = (SimulationDensityContainerData) connector.getSource();
				densityStr = "density." + densityContainer.getDensity().getName() + ".matrix(position.y.value, position.x.value)";
				flowStr  = submodel.getName() + "." + connector.getValve().getName() + ".value";
				out.println(densityStr + " = " + densityStr + " - " + flowStr + "; % consume");
			}
			// produce
			if (connector.getTarget() instanceof SimulationDensityContainerData) {
				densityContainer = (SimulationDensityContainerData) connector.getTarget();
				densityStr = "density." + densityContainer.getDensity().getName() + ".matrix(position.y.value, position.x.value)";
				flowStr  = submodel.getName() + "." + connector.getValve().getName() + ".value";
				out.println(densityStr + " = " + densityStr + " + " + flowStr + "; % produce");
			}
		}
		out.newline();
	}

	private void printDiffusionCalculation(CodeOutput out, SimulationXYModel xyModel, double diffusionCoefficient) {
		for (DensityData density : xyModel.getDensity()) {
				out.println("density." + density.getName() + ".matrix = sim_diffusion(density." + density.getName() + ".matrix, sim_dt, " + diffusionCoefficient + ");");
		}
		out.newline();
	}

	private void printGradientCalculation(CodeOutput out, SimulationXYModel xyModel) {
		HashSet<DensityData> densitySet;
		Vector<FlowConnectorData> connectorList;
		SimulationDensityContainerData densityContainer;

		// make a set of all densities, which will change over time
		densitySet = new HashSet<DensityData>();
		for (MesoData meso : xyModel.getMeso()) {
			connectorList = meso.getSubmodel().getModel().getFlowConnectors();
			for (FlowConnectorData connector : connectorList) {
				if (connector.getSource() instanceof SimulationDensityContainerData) {
					densityContainer = (SimulationDensityContainerData) connector.getSource();
					densitySet.add(densityContainer.getDensity());
				}
				if (connector.getTarget() instanceof SimulationDensityContainerData) {
					densityContainer = (SimulationDensityContainerData) connector.getTarget();
					densitySet.add(densityContainer.getDensity());
				}
			}
		}

		// calculate gradient only, if it's in the set of used densities
		for (DensityData density : xyModel.getDensity()) {
			if (densitySet.contains(density)) {
				// gradient
				out.println("[ density." + density.getName() + ".grad.dx " + ", density." + density.getName() + ".grad.dy ] = sim_gradient(density." + density.getName() + ".matrix);");
			}
		}
		out.newline();
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
		} else {
			out.println(meso.getName() + ".y = 0;");
		}
	}

	/**
	 * In flow function
	 *
	 * @param out
	 * @param submodel
	 */
	private void printFlowFunctionVectorToContainer(CodeOutput out, SubModel submodel) {
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

	private void printContainerCalculations(CodeOutput out, MesoData meso, String flowFunction) {
		String mesoName = meso.getName();

		if (meso.getSubmodel().getModel().getSimulationContainer().size() > 0) {
			out.printComment("Reset intermediate steps");
			out.println(mesoName + ".k = zeros(length(" + mesoName + ".y), 4);");
			out.newline();

			out.printComment("Calculate k-vector without saving submodel and density");
			for (int i = 1; i <= 4; i++) {
				out.println("[ " + mesoName + ".k(:, " + i + ") tmp_model tmp_density ] = " + flowFunction + "(sim_time + sim_dt * sim_c(" + i + "), " + mesoName + ".y + sim_dt * " + mesoName + ".k * sim_a(:," + i + "), " + mesoName + ".submodel, " + mesoName + ".position.approx, density);");
			}
			out.newline();

			out.printComment("dy");
			out.println(mesoName + ".dy = " + mesoName + ".k * sim_b;");
			out.newline();

			out.printComment("y = y + dt * dy");
			out.println(mesoName + ".y = " + mesoName + ".y + sim_dt * " + mesoName + ".dy;");
			out.newline();
		}

		if (meso.getSubmodel().getModel().getSimulationContainer().size() > 0 || meso.getSubmodel().getModel().getSimulationDensityContainer().size() > 0) {
			out.printComment("Calculate and save submodel and density");
			out.println("[ tmp_k " + mesoName + ".submodel density ] = " + flowFunction + "(sim_time, " + mesoName + ".y, " + mesoName + ".submodel, " + mesoName + ".position.approx, density);");
		}
	}

	/**
	 * In main method
	 *
	 * @param out
	 * @param prefix
	 * @param submodel
	 */
	private void printMainVectorToContainer(CodeOutput out, String prefix, SubModel submodel) {
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
