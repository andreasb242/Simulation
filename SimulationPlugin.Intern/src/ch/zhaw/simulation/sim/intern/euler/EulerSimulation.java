package ch.zhaw.simulation.sim.intern.euler;

import java.security.InvalidAlgorithmParameterException;

import ch.zhaw.simulation.plugin.data.SimulationCollection;
import org.nfunk.jep.ParseException;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.sim.intern.main.AbstractSimulation;
import ch.zhaw.simulation.sim.intern.main.Simulation;
import ch.zhaw.simulation.sim.intern.model.SimulationAttachment;
import ch.zhaw.simulation.sim.intern.model.SimulationContainerAttachment;
import ch.zhaw.simulation.sim.intern.model.SimulationFlowAttachment;

public class EulerSimulation extends AbstractSimulation {

	public EulerSimulation(Simulation sim) {
		super(sim);
	}

	public SimulationCollection simulate() throws ParseException, InvalidAlgorithmParameterException {
		SimulationCollection series = getSeries();

		for (time = 0; time < endTime && !isCancelled(); time += dt) {
			setProgress((int) (time / endTime * 100));
			calcFlowValues();
			calcContainers();
			calcParameters();

			for (SimulationContainerData c : model.getSimulationContainer()) {
				Object v = ((SimulationContainerAttachment) c.attachment).getContainerValue();
				((SimulationAttachment) c.attachment).serie.add(time, v);
			}
		}

		return series;
	}

	protected void calcFlowValues() throws ParseException {
		for (FlowConnectorData c : model.getFlowConnectors()) {
			calcFlowValue(c);
		}
	}

	protected void calcFlowValue(FlowConnectorData c) throws ParseException {
		FlowValveData d = c.getValve();

		Object value;
		try {
			value = ((SimulationAttachment) d.attachment).calc(time, dt);
		} catch (NullPointerException e) {
			Errorhandler.logError(e, "Fehler beim parsen von Connector: " + c.toString());
			throw e;
		}

		if (value == null) {
			throw new NullPointerException("value == null");
		}

		((SimulationFlowAttachment) d.attachment).setNextFlowValue(value);
		((SimulationFlowAttachment) d.attachment).serie.add(time, value);
	}

	protected void calcContainers() throws ParseException {
		for (FlowConnectorData c : model.getFlowConnectors()) {
			doFlow(c);
		}
	}

	protected void doFlow(FlowConnectorData c) throws ParseException {
		Object v = ((SimulationFlowAttachment) c.getValve().attachment).getNextFlowValue();
		AbstractSimulationData source = c.getSource();
		AbstractSimulationData target = c.getTarget();

		if (source instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (source instanceof SimulationContainerData) {
			((SimulationContainerAttachment) ((SimulationContainerData) source).attachment).minusContainerValue(v, dt);
		} else {
			throw new RuntimeException("Source of flow is: " + source.getClass());
		}

		if (target instanceof InfiniteData) {
			// Nichts zu berechnen...
		} else if (target instanceof SimulationContainerData) {
			((SimulationContainerAttachment) ((SimulationContainerData) target).attachment).addContainerValue(v, dt);
		} else {
			throw new RuntimeException("Target of flow is: " + target.getClass());
		}
	}
}
