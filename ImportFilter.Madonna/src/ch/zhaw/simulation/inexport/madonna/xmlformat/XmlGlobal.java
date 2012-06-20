package ch.zhaw.simulation.inexport.madonna.xmlformat;

public class XmlGlobal {
	private String name;
	private String formula;

	public XmlGlobal(String name, String formula) {
		this.name = name;
		this.formula = formula;
	}

	public String getName() {
		return name;
	}

	public String getFormula() {
		return formula;
	}
}
