package ch.zhaw.simulation.model.xy;

import java.util.Vector;

import ch.zhaw.simulation.model.NamedFormulaData;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;

/**
 * Meso Data with submodel
 * 
 * !! NamedFormulaData is not correct implemented !!
 * 
 * Because there are two formulas, there is a getter for getDataX() and
 * getDataY(), this is a workaround because our interface cannot handle two
 * formulas (and it makes no sense to extend this interface)
 * 
 * 
 * @author Andreas Butti
 * 
 */
public class MesoData extends AbstractNamedSimulationData {

	private SubModel submodel;

	public enum Derivative {
		/**
		 * x, y: Keine Ableitung (z.B. sin(x) als direkt Zeitabhängige Funktion
		 */
		NO_DERIVATIVE,

		/**
		 * ẋ, ẏ: Erste Ableitung, z.B. 1 um stetig nach links zu bewegen
		 */
		FIRST_DERIVATIVE,

		/**
		 * ẍ, ÿ: Zweite Ableitung
		 */
		SECOND_DERIVATIVE
	}

	private Derivative derivative = Derivative.FIRST_DERIVATIVE;

	// Formula for X and Y direction
	private String formulaX = "";
	private String formulaY = "";

	private NamedFormulaData dataX = new NamedFormulaData() {

		@Override
		public String getName() {
			return MesoData.this.getName();
		}

		@Override
		public void setName(String name) {
			// Note implemented
		}

		@Override
		public String getFormula() {
			return formulaX;
		}

		@Override
		public void setFormula(String formula) {
			if (formula == null) {
				throw new NullPointerException("formula == null");
			}
			formulaX = formula;
		}

		@Override
		public void setFormula(String formula, Status status, String statusText) {
			// Note implemented
		}

		@Override
		public void setUsedGlobals(Vector<SimulationGlobalData> usedGlobals) {
			// Note implemented
		}

		@Override
		public NamedFormulaData getRealNamedFormulaData() {
			return MesoData.this;
		}
	};

	private NamedFormulaData dataY = new NamedFormulaData() {

		@Override
		public String getName() {
			return MesoData.this.getName();
		}

		@Override
		public void setName(String name) {
			// Note implemented
		}

		@Override
		public String getFormula() {
			return formulaY;
		}

		@Override
		public void setFormula(String formula) {
			if (formula == null) {
				throw new NullPointerException("formula == null");
			}
			formulaY = formula;
		}

		@Override
		public void setFormula(String formula, Status status, String statusText) {
			// Note implemented
		}

		@Override
		public void setUsedGlobals(Vector<SimulationGlobalData> usedGlobals) {
			// Note implemented
		}

		@Override
		public NamedFormulaData getRealNamedFormulaData() {
			return MesoData.this;
		}

	};

	public MesoData(int x, int y) {
		super(x, y);
	}

	@Override
	public String getDefaultName() {
		return "m";
	}

	@Override
	public int getWidth() {
		return 30;
	}

	@Override
	public int getHeight() {
		return 30;
	}

	public Derivative getDerivative() {
		return derivative;
	}

	public void setDerivative(Derivative derivative) {
		if (derivative == null) {
			throw new NullPointerException("derivative == null");
		}

		this.derivative = derivative;
	}

	public String getFormulaX() {
		return formulaX;
	}

	public String getFormulaY() {
		return formulaY;
	}

	public NamedFormulaData getDataX() {
		return dataX;
	}

	public NamedFormulaData getDataY() {
		return dataY;
	}

	public void setSubmodel(SubModel submodel) {
		this.submodel = submodel;
		if (submodel == null) {
			setStaus(Status.SYNTAX_ERROR, "Kein Submodell");
		} else {
			setStaus(Status.SYNTAX_OK, null);
		}
	}

	public SubModel getSubmodel() {
		return submodel;
	}
}
