package ch.zhaw.simulation.plugin.matlab.optimizer;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.*;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.plugin.matlab.FlowModelAttachment;
import ch.zhaw.simulation.plugin.matlab.XYModelAttachment;
import org.nfunk.jep.ParseException;

import java.util.Vector;

/**
 * @author: bachi
 */
public class XYModelOptimizer implements ModelOptimizer {
	private SimulationXYModel xyModel;
	private Parser parser = new Parser();

	public XYModelOptimizer(SimulationXYModel xyModel) {
		this.xyModel = xyModel;
		if (xyModel == null) {
			throw new NullPointerException("model == null");
		}
	}

	public void optimize() throws SimulationModelException {
		AbstractNamedSimulationData	namedData;
		MesoData            		mesoData;

		initModelForSimulation();
		for (AbstractSimulationData data : xyModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {
				namedData = (AbstractNamedSimulationData) data;
				if (namedData instanceof MesoData) {
					mesoData = (MesoData) namedData;
					// TODO: anders behandeln!
					if (mesoData.getDataX().getFormula() == null) {
						throw new EmptyDerivativeException(mesoData.getDataX());
					}
					if (mesoData.getDataY().getFormula() == null) {
						throw new EmptyDerivativeException(mesoData.getDataX());
					}

					// Optimize submodel
					new FlowModelOptimizer(mesoData.getSubmodel().getModel()).optimize();
				} else {
					parseFormula(namedData);
				}
			}
		}

		// TODO: Hack!
		Vector<String> predefined = new Vector<String>();
		predefined.add("x");
		predefined.add("y");

		// Iterate over all initial densities (because they aren't AbstractNamedSimulationData)
		for (DensityData density : xyModel.getDensity()) {
			parser.checkCode(density.getFormula(), density, xyModel, new Vector<AbstractNamedSimulationData>(), predefined, density.getName());
		}
	}

	private void initModelForSimulation() {
		for (AbstractSimulationData data : xyModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {
				AbstractNamedSimulationData namedData = (AbstractNamedSimulationData) data;
				namedData.attachment = new XYModelAttachment();
			}
		}
	}

	private void parseFormula(AbstractNamedSimulationData namedData) throws EmptyFormulaException, NotUsedException, CompilerError, SimulationParserException, VarNotFoundException {
		XYModelAttachment attachment = (XYModelAttachment) namedData.attachment;

		try {
			System.out.println(namedData.getName());
			if (!(namedData instanceof MesoData)) {
                attachment.optimize(parser.checkCode(namedData.getFormula(), namedData, xyModel, new Vector<AbstractNamedSimulationData>(), namedData.getName()));
            }
		} catch (ParseException e) {
			throw new SimulationParserException(namedData, e);
		}
	}
}
