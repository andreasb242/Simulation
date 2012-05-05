package ch.zhaw.simulation.plugin.matlab.codegen;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author: bachi
 */
public class XYCodeGenerator extends AbstractCodeGenerator {

	public static final String FILENAME = "xy_main.m";

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
		CodeOutput out;

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + FILENAME));

		out.println("clear all;");
		out.println("close all;");
		out.println("clc;");
		out.newline();
		
		SimulationXYModel xyModel = doc.getXyModel();

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
		out.println("xymodel.x = x - xymodel.zero.x - 1;");
		out.println("xymodel.y = y - xymodel.zero.y - 1;");

		Vector<DensityData> densityList = xyModel.getDensity();
		Iterator<DensityData> densityIter = densityList.iterator();

		// iterate over all densities in the model for every x and y
		while (densityIter.hasNext()) {
			DensityData density = densityIter.next();
			String functionName = "xy_" + density.getName() + "_function";

			// generate density function (formula to matrix transformation) as a new file
			generateDensityFunctionFile(functionName, density);

			// assign function(model-x, model-y) to matrix(matlab-x, matlab-y)
			out.println(density.getName() + ".matrix(x, y) = " + functionName + "(xymodel.x, xymodel.y);");
		}
		out.detent();
		out.println("end;");
		out.detent();
		out.println("end;");
		out.newline();

		/*** initialize meso compartments ***/
		out.printComment("init meso compartments");
		Vector<MesoData> mesoList = xyModel.getMeso();
		Iterator<MesoData> mesoIter = mesoList.iterator();

		// iterate over meso-comp.
		while (mesoIter.hasNext()) {
			MesoData meso = mesoIter.next();
			out.printComment("init " + meso.getName());

			// set x/y
			out.println(meso.getName() + ".x = " + meso.getXCenter());
			out.println(meso.getName() + ".y = " + meso.getYCenter());

			/*** init submodel ***/
			SubModel submodel = meso.getSubmodel();

			// add connector
			Iterator<FlowConnectorData> connectorIter = submodel.getModel().getFlowConnectors().iterator();
			while (connectorIter.hasNext()) {
				FlowConnectorData connector = connectorIter.next();
				out.println(meso.getName() + ".submodel." + connector.getValve().getName() + " = " + connector.getValve().getFormula());
			}

			// add container
			Iterator<SimulationContainerData> containerIter = submodel.getModel().getSimulationContainer().iterator();
			while (containerIter.hasNext()) {
				SimulationContainerData container = containerIter.next();
				out.println(meso.getName() + ".submodel." + container.getName() + " = " + container.getFormula());
			}

			// add parameter
			Iterator<SimulationParameterData> parameterIter = submodel.getModel().getSimulationParameter().iterator();
			while (parameterIter.hasNext()) {
				SimulationParameterData parameter = parameterIter.next();
				out.println(meso.getName() + ".submodel." + parameter.getName() + " = " + parameter.getFormula());
			}

			// add density
			Iterator<SimulationDensityContainerData> densityContainer = submodel.getModel().getSimulationDensityContainer().iterator();
			while (parameterIter.hasNext()) {
				SimulationParameterData parameter = parameterIter.next();
				out.println(meso.getName() + ".submodel." + parameter.getName() + " = " + parameter.getFormula());
			}


			out.newline();
		}

		out.printComment("init meso compartments");


		out.println("fclose(fopen('matlab_finish.txt', 'w'));");

		out.close();
	}


	private void generateDensityFunctionFile(String functionName, DensityData density) throws Exception {
		CodeOutput out;

		out = new CodeOutput(new FileOutputStream(getWorkingFolder() + File.separator + functionName + ".m"));

		out.println("function result = " + functionName + "(x, y)");
		out.indent();
		out.println("result = " + density.getFormula() + ";");
		out.detent();
		out.println("end");
		
		out.close();
	}
}
