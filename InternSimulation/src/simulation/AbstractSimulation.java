package simulation;

import java.security.InvalidAlgorithmParameterException;

import javax.swing.SwingWorker;

import math.CompilerError;

import org.nfunk.jep.ParseException;

import ch.zhaw.simulation.model.SimulationDocument;
import ch.zhaw.simulation.model.SimulationParameter;
import ch.zhaw.simulation.model.simulation.SimulationModel;

import simulation.data.SimulationCollection;

public abstract class AbstractSimulation extends SwingWorker<SimulationCollection, Object> {
	protected double endTime;
	protected double startTime;

	protected double dt;
	protected double time;

	protected SimulationDocument model;

	private SimulationCollection series;

	public AbstractSimulation(Simulation sim) {
		this.model = sim.getModel();
		SimulationModel simModel = model.getSimModel();

		startTime = simModel.getStartTime();
		endTime = simModel.getEndTime();
		dt = simModel.getDt();
	}

	protected void calcParameters() throws ParseException {
		for (SimulationParameter p : model.getSimulationParameter()) {
			calcParameter(p);
		}
	}

	private void calcParameter(SimulationParameter p) throws ParseException {
		if (!p.a.serie.isConstValue()) {
			p.a.serie.add(time, p.a.calc(time, dt));
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
