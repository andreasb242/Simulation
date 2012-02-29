package ch.zhaw.simulation.model.xy;

/**
 * The Density startconfiguration
 * 
 * @author Andreas Butti
 */
public class Density {
	private String formula = "";
	private String name;
	private String description;

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

}
