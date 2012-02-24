package ch.zhaw.simulation.sim.mo;

import java.util.Vector;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.math.exception.EmptyFormulaException;
import ch.zhaw.simulation.math.exception.NotUsedException;
import ch.zhaw.simulation.math.exception.SimulationModelException;
import ch.zhaw.simulation.math.exception.SimulationParserException;
import ch.zhaw.simulation.math.exception.VarNotFoundException;
import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationContainer;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.sim.mo.MOAttachment.VarNotFoundExceptionTmp;

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
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				parseFormula((NamedSimulationObject) d);
			}
		}

		// Wenn möglich optimieren (Parameter die nur von konstanten Parametern
		// abhängig sind auch konstant machen)
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				optimizeStatic((NamedSimulationObject) d);
			}
		}

		// Constwerte auslesen
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				calculateConstValues((NamedSimulationObject) d);
			}
		}
	}

	private void initModelForSimulation() {
		for (SimulationObject d : model.getData()) {
			if (d instanceof NamedSimulationObject) {
				NamedSimulationObject n = (NamedSimulationObject) d;
				n.a = new MOAttachment();
			}
		}

		for (FlowConnector c : model.getFlowConnectors()) {
			c.getValve().a = new MOAttachment();
		}
	}

	private void parseFormula(NamedSimulationObject d) throws EmptyFormulaException, NotUsedException, CompilerError, SimulationParserException,
			VarNotFoundException {
		MOAttachment a = (MOAttachment) d.a;

		Vector<NamedSimulationObject> sources = model.getSource(d);
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
	private void optimizeStatic(NamedSimulationObject d) throws SimulationParserException {
		MOAttachment a = (MOAttachment) d.a;
		try {
			a.optimizeStatic(model);
			
			a.calcOrder();
		} catch (ParseException e) {
			throw new SimulationParserException(d, e);
		}
	}

	private void calculateConstValues(NamedSimulationObject d) {
		if (d instanceof SimulationContainer) {
			// Container sind nur Konstant wenn keine Ein- Und
			// Ausflüsse vorhanden sind!

			SimulationContainer c = (SimulationContainer) d;
			if (model.hasFlowConnectors(c)) {
				// ein und / oder Ausflüsse vorhanden
				return;
			}
		}

		MOAttachment a = (MOAttachment) d.a;

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
