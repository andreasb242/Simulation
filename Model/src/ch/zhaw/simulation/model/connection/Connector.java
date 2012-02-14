package ch.zhaw.simulation.model.connection;

import ch.zhaw.simulation.model.SimulationObject;

public abstract class Connector<T extends SimulationObject> {
	private T source;
	private T target;

	public Connector(T source, T target) {
		this.source = source;
		this.target = target;
	}

	public T getSource() {
		return source;
	}

	public T getTarget() {
		return target;
	}

	public void setSource(T source) {
		this.source = source;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	public void dispose() {
		target = null;
		source = null;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + ": " + connectorString();
	}

	protected String connectorString() {
		String from = "null";
		String to = "null";
		
		if(source != null) {
			from = source.toString();
		}
		if(target != null) {
			to = target.toString();
		}
		
		return "from " + from  + " to " + to;
	}
}
