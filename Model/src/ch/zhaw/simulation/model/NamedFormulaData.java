package ch.zhaw.simulation.model;

import java.util.Vector;

import ch.zhaw.simulation.model.element.SimulationGlobalData;

public interface NamedFormulaData {
	/**
	 * The States of this component, if the formula is OK
	 */
	public static enum Status {
		NOT_PARSED, SYNTAX_OK, SYNTAX_ERROR
	};

	/**
	 * @return The name of this object
	 */
	public String getName();

	/**
	 * Sets the name of this object
	 * 
	 * @param name
	 *            The new name
	 */
	public void setName(String name);

	/**
	 * @return The formula
	 */
	public String getFormula();

	/**
	 * Sets the formula
	 * 
	 * @param formula
	 *            The formula, should not be null
	 */
	public void setFormula(String formula);

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
	void setFormula(String formula, Status status, String statusText);

	/**
	 * If this object references globals, this are saved here during parsing and
	 * checking formula
	 * 
	 * @param usedGlobals
	 *            The vector to save
	 */
	public void setUsedGlobals(Vector<SimulationGlobalData> usedGlobals);
}