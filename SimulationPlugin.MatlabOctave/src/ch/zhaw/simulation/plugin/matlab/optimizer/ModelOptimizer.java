package ch.zhaw.simulation.plugin.matlab.optimizer;

import ch.zhaw.simulation.math.exception.SimulationModelException;

/**
 * @author: bachi
 */
public interface ModelOptimizer {
	public void optimize() throws SimulationModelException;
}
