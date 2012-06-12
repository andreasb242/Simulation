package ch.zhaw.simulation.plugin.matlab.optimizer;

import java.util.Vector;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.EmptyFormulaException;
import ch.zhaw.simulation.math.exception.NotUsedException;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.math.exception.SimulationParserException;
import ch.zhaw.simulation.math.exception.VarNotFoundException;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.plugin.matlab.XYModelAttachment;

/**
 * This class optimizes an XY-model
 *
 *
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
		initModelForSimulation();
		for (AbstractSimulationData data : xyModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {
				parseFormula((AbstractNamedSimulationData) data);
			}
		}

		// TODO: Hack!
		Vector<String> predefined = new Vector<String>();
		predefined.add("x");
		predefined.add("y");

		// Iterate over all initial densities (because they aren't AbstractNamedSimulationData)
		// and syntax check formula
		for (DensityData density : xyModel.getDensity()) {
			// TODO: replace parser variables with MATLAB variables and save in attachment
			parser.checkCode(density.getFormula(), density, xyModel, new Vector<AbstractNamedSimulationData>(), predefined, density.getName());
		}
	}

	/**
	 * Creates an attachment for every data (meso, global) in a xy-model
	 * and assigns to this data.
	 */
	private void initModelForSimulation() {
		for (AbstractSimulationData data : xyModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {
				AbstractNamedSimulationData namedData = (AbstractNamedSimulationData) data;
				namedData.attachment = new XYModelAttachment();
			}
		}
	}

	private void parseFormula(AbstractNamedSimulationData namedData) throws EmptyFormulaException, NotUsedException, CompilerError, SimulationParserException, VarNotFoundException, SimulationModelException {
		MesoData mesoData;
		XYModelAttachment attachment = (XYModelAttachment) namedData.attachment;

		try {
			System.out.println(namedData.getName());

			// if its a meso...
			if (namedData instanceof MesoData) {
				mesoData = (MesoData) namedData;

				// optimize movements / motions of a meso
				attachment.optimizeDataX(parser.checkCode(mesoData.getDataX().getFormula(), namedData, xyModel, new Vector<AbstractNamedSimulationData>(), namedData.getName()));
				attachment.optimizeDataY(parser.checkCode(mesoData.getDataY().getFormula(), namedData, xyModel, new Vector<AbstractNamedSimulationData>(), namedData.getName()));

				// optimize submodel of a meso
				new FlowModelOptimizer(mesoData.getSubmodel().getModel()).optimize();

			// ... if not, optimize normal (global)
			} else {
                attachment.optimize(parser.checkCode(namedData.getFormula(), namedData, xyModel, new Vector<AbstractNamedSimulationData>(), namedData.getName()));
            }
		} catch (ParseException e) {
			throw new SimulationParserException(namedData, e);
		}
	}
}
