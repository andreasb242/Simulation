package ch.zhaw.simulation.inexport.madonna;

import java.awt.Point;

public abstract class MadonnaFormulaElement extends MadonnaElement {

	private String name;
	private String formula;

	public MadonnaFormulaElement(String name, Point pos, String formula) {
		super(pos);
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
