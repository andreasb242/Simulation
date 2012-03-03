package ch.zhaw.simulation.plugin.matlab;

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
import ch.zhaw.simulation.model.element.SimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.plugin.matlab.MatlabAttachment.VarNotFoundExceptionTmp;

public class ModelOptimizer {
	private SimulationFlowModel model;
	private Parser parser = new Parser();

	public ModelOptimizer(SimulationFlowModel model) {
		this.model = model;
		if (model == null) {
			throw new NullPointerException("model == null");
		}
	}

	public void optimize() throws SimulationModelException {
		initModelForSimulation();
		for (SimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				parseFormula((AbstractNamedSimulationData) d);
			}
		}

		// Wenn möglich optimieren (Parameter die nur von konstanten Parametern
		// abhängig sind auch konstant machen)
		for (SimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				optimizeStatic((AbstractNamedSimulationData) d);
			}
		}

		// Constwerte auslesen
		for (SimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				calculateConstValues((AbstractNamedSimulationData) d);
			}
		}
	}

	private void initModelForSimulation() {
		for (SimulationData d : model.getData()) {
			if (d instanceof AbstractNamedSimulationData) {
				AbstractNamedSimulationData n = (AbstractNamedSimulationData) d;
				n.a = new MatlabAttachment();
			}
		}

		for (FlowConnectorData c : model.getFlowConnectors()) {
			c.getValve().a = new MatlabAttachment();
		}
	}

	private void parseFormula(AbstractNamedSimulationData d) throws EmptyFormulaException, NotUsedException, CompilerError, SimulationParserException,
			VarNotFoundException {
		MatlabAttachment a = (MatlabAttachment) d.a;

		Vector<AbstractNamedSimulationData> sources = model.getSource(d);
		a.setSources(sources);

		a.setParsed(parser.checkCode(d.getFormula(), d, model, sources, d.getName()));
		try {
			a.assigneSourcesVars();
		} catch (VarNotFoundExceptionTmp e1) {
			throw new VarNotFoundException(d, e1.getMessage(), e1);
		}
		try {
			a.optimize();
		} catch (ParseException e) {
			throw new SimulationParserException(d, e);
		}
	}

	/**
	 * Optimize out calculations which are static
	 */
	private void optimizeStatic(AbstractNamedSimulationData d) throws SimulationParserException {
		MatlabAttachment a = (MatlabAttachment) d.a;
		try {
			a.optimizeStatic(model);
			
			a.calcOrder();
		} catch (ParseException e) {
			throw new SimulationParserException(d, e);
		}
	}

	private void calculateConstValues(AbstractNamedSimulationData d) {
		if (d instanceof SimulationContainerData) {
			// Container sind nur Konstant wenn keine Ein- Und
			// Ausflüsse vorhanden sind!

			SimulationContainerData c = (SimulationContainerData) d;
			if (model.hasFlowConnectors(c)) {
				// ein und / oder Ausflüsse vorhanden
				return;
			}
		}

		MatlabAttachment a = (MatlabAttachment) d.a;

		Object value = a.getValue();
		if (value != null) {
			if (value instanceof Double) {
				a.setConstValue(((Double) value).doubleValue());
			} else {
				new SimulationModelException(d, "Cannot set const value typeof " + value.getClass().getName());
			}
		}
	}
}
