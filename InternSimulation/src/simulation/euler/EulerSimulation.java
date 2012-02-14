package simulation.euler;

import java.security.InvalidAlgorithmParameterException;

import math.CompilerError;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.model.InfiniteData;
import ch.zhaw.simulation.model.SimulationContainer;
import ch.zhaw.simulation.model.SimulationObject;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;

import simulation.AbstractSimulation;
import simulation.Simulation;
import simulation.data.SimulationCollection;
import butti.javalibs.errorhandler.Errorhandler;

public class EulerSimulation extends AbstractSimulation {

	public EulerSimulation(Simulation sim) {
		super(sim);
	}

	public SimulationCollection simulate() throws CompilerError, ParseException, InvalidAlgorithmParameterException {
		SimulationCollection series = getSeries();

		for (time = 0; time < endTime && !isCancelled(); time += dt) {
			setProgress((int) (time / endTime * 100));
			calcFlowValues();
			calcContainers();
			calcParameters();

			for (SimulationContainer c : model.getSimulationContainer()) {
				Object v = c.a.getContainerValue();
				c.a.serie.add(time, v);
			}
		}

		return series;
	}

	protected void calcFlowValues() throws CompilerError, ParseException {
		for (FlowConnector c : model.getFlowConnectors()) {
			calcFlowValue(c);
		}
	}

	protected void calcFlowValue(FlowConnector c) throws CompilerError, ParseException {
		FlowParameterPoint d = c.getParameterPoint();

		Object value;
		try {
			value = d.a.calc(time, dt);
		} catch (NullPointerException e) {
			Errorhandler.logError(e, "Fehler beim parsen von Connector: " + c.toString());
			throw e;
		}

		if (value == null) {
			throw new NullPointerException("value == null");
		}

		d.setNextFlowValue(value);
		d.a.serie.add(time, value);
	}

	protected void calcContainers() throws ParseException {
		for (FlowConnector c : model.getFlowConnectors()) {
			doFlow(c);
		}
	}

	protected void doFlow(FlowConnector c) throws ParseException {
		Object v = c.getParameterPoint().getNextFlowValue();
		SimulationObject source = c.getSource();
		SimulationObject target = c.getTarget();

		if (source instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (source instanceof SimulationContainer) {
			((SimulationContainer) source).a.minusContainerValue(v, dt);
		} else {
			throw new RuntimeException("Source of flow is: " + source.getClass());
		}

		if (target instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (target instanceof SimulationContainer) {
			((SimulationContainer) target).a.addContainerValue(v, dt);
		} else {
			throw new RuntimeException("Target of flow is: " + target.getClass());
		}
	}
}
