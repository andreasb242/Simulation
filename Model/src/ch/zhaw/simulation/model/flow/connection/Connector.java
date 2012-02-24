package ch.zhaw.simulation.model.flow.connection;

import ch.zhaw.simulation.model.flow.SimulationObject;

/**
 * This is a connector within the flow model
 * 
 * @author Andreas Butti
 * 
 * @param <T>
 *            WHAT is connected
 */
public abstract class Connector<T extends SimulationObject> {
	/**
	 * The source of this connector "from"
	 */
	private T source;

	/**
	 * The target of this connector "to"
	 */
	private T target;

	public Connector(T source, T target) {
		this.source = source;
		this.target = target;
	}

	/**
	 * Return the source of this connector
	 * 
	 * @return "from"
	 */
	public T getSource() {
		return source;
	}

	/**
	 * Returns the target of this connector
	 * 
	 * @return "to"
	 */
	public T getTarget() {
		return target;
	}

	/**
	 * Sets the source of this connector
	 * 
	 * @param source
	 *            "from"
	 */
	public void setSource(T source) {
		this.source = source;
	}

	/**
	 * Sets the target of this connector
	 * 
	 * @param target
	 *            "to"
	 */
	public void setTarget(T target) {
		this.target = target;
	}

	/**
	 * Disposes this connector
	 */
	public void dispose() {
		target = null;
		source = null;
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + connectorString();
	}

	/**
	 * Creates a string "from xx to yy" used for debugging
	 */
	protected String connectorString() {
		String from = "null";
		String to = "null";

		if (source != null) {
			from = source.toString();
		}
		if (target != null) {
			to = target.toString();
		}

		return "from " + from + " to " + to;
	}
}
