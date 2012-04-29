package ch.zhaw.simulation.plugin.matlab.optimizer;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.*;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.plugin.matlab.MatlabAttachment;
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
				namedData.attachment = new MatlabAttachment();
			}
		}
	}

	private void parseFormula(AbstractNamedSimulationData namedData) throws EmptyFormulaException, NotUsedException, CompilerError, SimulationParserException, VarNotFoundException {
		MatlabAttachment attachment = (MatlabAttachment) namedData.attachment;

		Vector<AbstractNamedSimulationData> sources = xyModel.getSource(namedData);
		attachment.setSources(sources);

		// Check formula and set attachment to parsed
		attachment.setParsed(parser.checkCode(namedData.getFormula(), namedData, xyModel, sources, namedData.getName()));
		try {
			attachment.assigneSourcesVars();
		} catch (MatlabAttachment.VarNotFoundExceptionTmp e1) {
			throw new VarNotFoundException(namedData, e1.getMessage(), e1);
		}
		try {
			attachment.optimize();
		} catch (ParseException e) {
			throw new SimulationParserException(namedData, e);
		}
	}
}
