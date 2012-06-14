package ch.zhaw.simulation.editor.xy.density;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;

import org.nfunk.jep.ParseException;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.densitydraw.AbstractDensityView;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.model.xy.SimulationXYModel;

/**
 * A Formula Density view
 * 
 * May can be optimized with a cache if arrows and color is displayed all is
 * calculated twice
 * 
 * @author Andreas Butti
 * 
 */
public class FormulaDensityDraw extends AbstractDensityView {
	private boolean noFormula = true;
	private Parser parser = new Parser();
	private boolean logarithmic = false;

	public FormulaDensityDraw(int width, int height, SimulationXYModel model) {
		super(width, height, model);
		parser.addVar("x", 0);
		parser.addVar("y", 0);
	}

	@Override
	public void updateImageAsynchron() {
		if (noFormula) {
			fireNoActionPerformed();
			return;
		}
		super.updateImageAsynchron();
	}

	@Override
	public synchronized void draw(Graphics2D g, int x, int y, ImageObserver observer) {
		if (noFormula == false) {
			super.draw(g, x, y, observer);
		}
	}

	@Override
	protected double valueFor(int x, int y) throws ParseException {
		Point z = model.getZero();

		parser.setVar("x", x - z.x);
		parser.setVar("y", y - z.x);

		return parser.evaluate();
	}

	/**
	 * 
	 * @param formula
	 *            if formula == "" no formula, if formula == null the view is
	 *            update
	 * @param logarithmic
	 */
	public void setFormula(String formula, boolean logarithmic) {
		if (formula == null) {
			return;
		}

		if ("".equals(formula)) {
			noFormula = true;
			return;
		}

		this.logarithmic = logarithmic;

		try {
			parser.simplyfy(formula);
			noFormula = false;
		} catch (ParseException e) {
			Errorhandler.showError(e, "Formel Fehler");
		}

		clearCache();
	}

	@Override
	protected boolean isLogarithmic() {
		return this.logarithmic;
	}
}