package ch.zhaw.simulation.model.xy;

/**
 * The Density startconfiguration
 * 
 * @author Andreas Butti
 */
public class Density {
	private String formula = "";

	public Density() {
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

}
