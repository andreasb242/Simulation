package ch.zhaw.simulation.inexport.madonna.binformat;

import java.awt.Point;

import ch.zhaw.simulation.inexport.madonna.FormulaImporter;

/**
 * Baseclass for all Formula elements:
 *  - Container
 *  - Valve
 *  - Parameter
 * 
 * @author Andreas Butti
 */
public abstract class MadonnaFormulaElement extends MadonnaElement {
	/**
	 * The name of the element (unique)
	 */
	private String name;

	/**
	 * The formula of the element
	 */
	private String formula;

	public MadonnaFormulaElement(String name, Point pos, String formula) {
		super(pos);
		this.name = name;
		this.formula = FormulaImporter.convert(formula);
	}

	public String getName() {
		return name;
	}

	public String getFormula() {
		return formula;
	}
}
