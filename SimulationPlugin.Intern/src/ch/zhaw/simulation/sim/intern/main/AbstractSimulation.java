package ch.zhaw.simulation.sim.intern.main;

import java.security.InvalidAlgorithmParameterException;

import javax.swing.SwingWorker;

import ch.zhaw.simulation.plugin.data.SimulationCollection;
import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.math.exception.CompilerError;
import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.simulation.SimulationConfiguration;
import ch.zhaw.simulation.plugin.StandardParameter;
import ch.zhaw.simulation.sim.intern.model.SimulationAttachment;

public abstract class AbstractSimulation extends SwingWorker<SimulationCollection, Object> {
	protected double endTime;
	protected double startTime;

	protected double dt;
	protected double time;

	protected SimulationFlowModel model;

	private SimulationCollection series;
	private SimulationDocument doc;

	public AbstractSimulation(Simulation sim) {
		this.doc = sim.getDocument();
		this.model = doc.getFlowModel();

		SimulationConfiguration config = doc.getSimulationConfiguration();
		startTime = config.getParameter(StandardParameter.START, 0);
		endTime = config.getParameter(StandardParameter.END, 5);
		dt = config.getParameter(StandardParameter.DT, 0.01);
	}

	protected void calcParameters() throws ParseException {
		for (SimulationParameterData p : model.getSimulationParameter()) {
			calcParameter(p);
		}
	}

	private void calcParameter(SimulationParameterData p) throws ParseException {
		if (!((SimulationAttachment) p.attachment).serie.isConstValue()) {
			((SimulationAttachment) p.attachment).serie.add(time, ((SimulationAttachment) p.attachment).calc(time, dt));
		}
	}

	@Override
	protected SimulationCollection doInBackground() throws Exception {
		return simulate();
	}

	protected abstract SimulationCollection simulate() throws CompilerError, ParseException, InvalidAlgorithmParameterException;

	public void setSeries(SimulationCollection series) {
		this.series = series;
	}

	public SimulationCollection getSeries() {
		return series;
	}
}
