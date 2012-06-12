package ch.zhaw.simulation.plugin.matlab.optimizer;

import java.util.Vector;

import ch.zhaw.simulation.math.exception.*;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;
import ch.zhaw.simulation.plugin.matlab.FlowModelAttachment;
import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.plugin.matlab.FlowModelAttachment.VarNotFoundExceptionTmp;

public class FlowModelOptimizer implements ModelOptimizer {
	private SimulationFlowModel flowModel;
	private Parser parser = new Parser();

	public FlowModelOptimizer(SimulationFlowModel flowModel) {
		this.flowModel = flowModel;
		if (flowModel == null) {
			throw new NullPointerException("model == null");
		}
	}

	public void optimize() throws SimulationModelException {
		initModelForSimulation();

		// iterate over all data (container, parameter, flow, density-container)
		for (AbstractSimulationData data : flowModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {

				// treat different if density-container
				if (data instanceof SimulationDensityContainerData) {
					SimulationDensityContainerData densityContainer = (SimulationDensityContainerData) data;
					if (densityContainer.getDensity() == null) {
						throw new EmptyDensityException(densityContainer);
					}
				} else {
					parseFormula((AbstractNamedSimulationData) data);
				}
			}
		}

		// Wenn möglich optimieren (Parameter die nur von konstanten Parametern
		// abhängig sind auch konstant machen)
		for (AbstractSimulationData data : flowModel.getData()) {
			if (data instanceof AbstractNamedSimulationData && !(data instanceof SimulationDensityContainerData)) {
				optimizeStatic((AbstractNamedSimulationData) data);
			}
		}

		// Constwerte auslesen
		for (AbstractSimulationData data : flowModel.getData()) {
			if (data instanceof AbstractNamedSimulationData && !(data instanceof SimulationDensityContainerData)) {
				calculateConstValues((AbstractNamedSimulationData) data);
			}
		}
	}

	/**
	 * For every AbstractSimulationData and FlowConnectorData,
	 * assign a new FlowModelAttachment
	 */
	private void initModelForSimulation() {
		for (AbstractSimulationData data : flowModel.getData()) {
			if (data instanceof AbstractNamedSimulationData) {
				AbstractNamedSimulationData namedData = (AbstractNamedSimulationData) data;
				namedData.attachment = new FlowModelAttachment();
			}
		}

		for (FlowConnectorData connector : flowModel.getFlowConnectors()) {
			connector.getValve().attachment = new FlowModelAttachment();
		}
	}

	/**
	 * Manipulate attachment of a data-object
	 *
	 * @param namedData
	 * @throws EmptyFormulaException
	 * @throws NotUsedException
	 * @throws CompilerError
	 * @throws SimulationParserException
	 * @throws VarNotFoundException
	 */
	private void parseFormula(AbstractNamedSimulationData namedData) throws EmptyFormulaException, NotUsedException, CompilerError, SimulationParserException, VarNotFoundException {
		FlowModelAttachment attachment = (FlowModelAttachment) namedData.attachment;

		// if namedData has sources (only for connectors)
		Vector<AbstractNamedSimulationData> sources = flowModel.getSource(namedData);
		attachment.setSources(sources);

		// Check formula and set attachment to parsed
		attachment.setParsed(parser.checkCode(namedData.getFormula(), namedData, flowModel, sources, namedData.getName()));
		try {
			attachment.assigneSourcesVars();
		} catch (VarNotFoundExceptionTmp e1) {
			throw new VarNotFoundException(namedData, e1.getMessage(), e1);
		}

		// Replace density-container


		try {
			attachment.optimize();
		} catch (ParseException e) {
			throw new SimulationParserException(namedData, e);
		}
	}

	/**
	 * Optimize out calculations which are static
	 */
	private void optimizeStatic(AbstractNamedSimulationData namedData) throws SimulationParserException {
		FlowModelAttachment attachment = (FlowModelAttachment) namedData.attachment;
		try {
			attachment.optimizeStatic(flowModel);
			
			attachment.calcOrder();
		} catch (ParseException e) {
			throw new SimulationParserException(namedData, e);
		}
	}

	private void calculateConstValues(AbstractNamedSimulationData namedData) {
		if (namedData instanceof SimulationContainerData) {
			// Container sind nur Konstant wenn keine Ein- Und
			// Ausflüsse vorhanden sind!

			SimulationContainerData c = (SimulationContainerData) namedData;
			if (flowModel.hasFlowConnectors(c)) {
				// ein und / oder Ausflüsse vorhanden
				return;
			}
		}

		FlowModelAttachment attachment = (FlowModelAttachment) namedData.attachment;

		Object value = attachment.getValue();
		if (value != null) {
			if (value instanceof Double) {
				attachment.setConstValue(((Double) value).doubleValue());
			} else {
				new SimulationModelException(namedData, "Cannot set const value typeof " + value.getClass().getName());
			}
		}
	}
}
