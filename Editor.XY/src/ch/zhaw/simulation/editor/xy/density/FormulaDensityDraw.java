package ch.zhaw.simulation.editor.xy.density;

import java.awt.image.BufferedImage;

import org.nfunk.jep.ParseException;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.densitydraw.AbstractDensityDraw;
import ch.zhaw.simulation.math.Parser;

public class FormulaDensityDraw extends AbstractDensityDraw {
	private boolean noFormula = true;
	private Parser parser = new Parser();
	private boolean logarithmic = false;

	public FormulaDensityDraw(int width, int height) {
		super(width, height);
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
	public BufferedImage getImage() {
		if (noFormula) {
			return null;
		}

		return super.getImage();
	}

	@Override
	protected float valueFor(int x, int y) throws ParseException {
		parser.setVar("x", x);
		parser.setVar("y", y);

		double v = parser.evaluate();

		if (this.logarithmic) {
			return (float) Math.log(v);
		}

		return (float) v;
	}

	public void setFormula(String formula, boolean logarithmic) {
		if (formula == null || "".equals(formula)) {
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
	}

}