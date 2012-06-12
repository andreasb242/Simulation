package ch.zhaw.simulation.model.xy;

import java.util.Vector;

import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.SimulationAttachment;
import ch.zhaw.simulation.model.element.SimulationGlobalData;

/**
 * The Density
 * 
 * @author Andreas Butti
 */
public class DensityData implements NamedFormulaData {
	private String formula = "";

	private String name;
	private String description;

	private boolean displayLogarithmic = false;

	/**
	 * The attachment, Temporary used for simulation / codegeneration
	 */
	public SimulationAttachment attachment;

	public DensityData() {
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		if (formula == null) {
			throw new NullPointerException("formula == null");
		}
		this.formula = formula;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setFormula(String formula, Status status, String statusText) {
		this.formula = formula;
	}

	@Override
	public void setStatus(Status status, String statusText) {
	}
	
	@Override
	public void setUsedGlobals(Vector<SimulationGlobalData> usedGlobals) {
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public NamedFormulaData getRealNamedFormulaData() {
		return this;
	}

	public boolean isDisplayLogarithmic() {
		return displayLogarithmic;
	}

	public void setDisplayLogarithmic(boolean displayLogarithmic) {
		this.displayLogarithmic = displayLogarithmic;
	}
}
