package ch.zhaw.simulation.model.xy;

import java.util.Vector;

import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;

/**
 * The Density startconfiguration
 * 
 * @author Andreas Butti
 */
public class DensityData implements NamedFormulaData {
	private String formula = "";
	private String name;
	private String description;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUsedGlobals(Vector<SimulationGlobalData> usedGlobals) {
	}

}
