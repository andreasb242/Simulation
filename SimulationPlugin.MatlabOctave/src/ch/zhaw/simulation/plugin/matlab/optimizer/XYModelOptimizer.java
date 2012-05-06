package ch.zhaw.simulation.plugin.matlab.optimizer;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.*;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
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
		initModelForSimulation();
		for (AbstractSimulationData data : xyModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {
				parseFormula((AbstractNamedSimulationData) data);
			}
		}
	}

	private void initModelForSimulation() {
		for (AbstractSimulationData data : xyModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {
				AbstractNamedSimulationData namedData = (AbstractNamedSimulationData) data;
				namedData.attachment = new FlowModelAttachment();
			}
		}
	}

	private void parseFormula(AbstractNamedSimulationData namedData) throws EmptyFormulaException, NotUsedException, CompilerError, SimulationParserException, VarNotFoundException {
		XYModelAttachment attachment = (XYModelAttachment) namedData.attachment;

		try {
			attachment.optimize(parser.checkCode(namedData.getFormula(), namedData, xyModel, null, namedData.getName()));
		} catch (ParseException e) {
			throw new SimulationParserException(namedData, e);
		}
	}
}
