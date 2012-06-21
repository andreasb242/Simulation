package ch.zhaw.simulation.sim.intern.model;

public class SimulationFlowAttachment extends SimulationAttachment{
	/**
	 * Flow Handling
	 */
	private Object nextFlowValue;

	public SimulationFlowAttachment() {
	}
	
	public void setNextFlowValue(Object value) {
		nextFlowValue = value;
	}

	public Object getNextFlowValue() {
		return nextFlowValue;
	}
}
