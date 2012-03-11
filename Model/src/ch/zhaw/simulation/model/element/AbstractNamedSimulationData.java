package ch.zhaw.simulation.model.element;

import ch.zhaw.simulation.model.SimulationAttachment;

/**
 * A named simulation object contains a name, a formula and an Attachment
 * 
 * The name has to be uniq within the model
 * 
 * The Attachment is used for Codegeneration / Simulation, this data is only
 * valid while the simulation is running, after this date is not used anymore
 * 
 * @author Andreas Butti
 */
public abstract class AbstractNamedSimulationData extends AbstractSimulationData implements Comparable<AbstractNamedSimulationData> {
	/**
	 * The name of the simulation object
	 */
	private String name;

	/**
	 * The formula of this object
	 */
	private String formula = "";

	/**
	 * Status for parsing / checking
	 */
	private Status staus = Status.NOT_PARSED;

	/**
	 * Status text, if something went wrong with parsing etc.
	 */
	private String statusText;

	/**
	 * The attachment, Temporary used for simulation / codegeneration
	 */
	public SimulationAttachment attachment;

	/**
	 * The States of this component, if the formula is OK
	 */
	public static enum Status {
		NOT_PARSED, SYNTAX_OK, SYNTAX_ERROR
	};

	public AbstractNamedSimulationData(int x, int y) {
		super(x, y);
	}

	/**
	 * @return The name of this object
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this object
	 * 
	 * @param name
	 *            The new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the formula
	 * 
	 * @param formula
	 *            The formula, should not be null
	 */
	public void setFormula(String formula) {
		if (formula == null) {
			throw new NullPointerException("formula == null");
		}
		this.formula = formula;
		this.staus = Status.NOT_PARSED;
		this.statusText = null;
	}

	/**
	 * Sets the formula
	 * 
	 * @param formula
	 *            The formula, should not be null
	 * @param status
	 *            The status if parsed
	 * @param statusText
	 *            The statustext
	 */
	public void setFormula(String formula, Status status, String statusText) {
		if (formula == null) {
			throw new NullPointerException("formula == null");
		}
		this.formula = formula;
		this.staus = status;
		this.statusText = statusText;
	}

	/**
	 * @return The formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * Sets the status
	 * 
	 * @param staus
	 *            The status of this formula
	 * @param statusText
	 *            The associated text
	 */
	public void setStaus(Status staus, String statusText) {
		this.staus = staus;
		this.statusText = statusText;
	}

	/**
	 * @return The status text
	 */
	public String getStatusText() {
		return statusText;
	}

	/**
	 * @return The status Text
	 */
	public Status getStaus() {
		return staus;
	}

	/**
	 * 
	 * If a new element is created it needs an unique name, this is created from
	 * a prefix, e.g. "c" for container, and an ID
	 * 
	 * 
	 * @return The name Prefix
	 */
	public abstract String getDefaultName();

	@Override
	public int compareTo(AbstractNamedSimulationData o) {
		return name.compareTo(o.name);
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getName();
	}
}
