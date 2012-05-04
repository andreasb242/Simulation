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

	private MesoDataFormula dataX = new MesoDataFormula(":X");
	private MesoDataFormula dataY = new MesoDataFormula(":Y");

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

	public MesoDataFormula getDataX() {
		return dataX;
	}

	public MesoDataFormula getDataY() {
		return dataY;
	}

	@Override
	public void setStaus(Status staus, String statusText) {
		throw new RuntimeException("Do not call this method on MesoData");
	}

	@Override
	public String getStatusText() {
		if (submodel == null) {
			return "Kein Submodell";
		} else if (this.dataX.getStatus() != Status.SYNTAX_OK) {
			return "X: " + this.dataX.getStatusText();
		} else if (this.dataY.getStatus() != Status.SYNTAX_OK) {
			return "Y: " + this.dataX.getStatusText();
		}

		return null;
	}

	@Override
	public Status getStaus() {
		if (this.submodel == null) {
			return Status.SYNTAX_ERROR;
		} else if (this.dataX.getStatus() != Status.SYNTAX_OK) {
			return Status.SYNTAX_ERROR;
		} else if (this.dataY.getStatus() != Status.SYNTAX_OK) {
			return Status.SYNTAX_ERROR;
		}

		return Status.SYNTAX_OK;
	}

	public void setSubmodel(SubModel submodel) {
		this.submodel = submodel;
	}

	public SubModel getSubmodel() {
		return submodel;
	}

	public class MesoDataFormula implements NamedFormulaData {
		private String formula = "";
		private Status status;
		private String statusText;
		private String nameSuffix;

		public MesoDataFormula(String nameSuffix) {
			this.nameSuffix = nameSuffix;
		}

		@Override
		public String getName() {
			return MesoData.this.getName() + nameSuffix;
		}

		@Override
		public void setName(String name) {
			// Note implemented
		}

		@Override
		public String getFormula() {
			return formula;
		}

		@Override
		public void setFormula(String formula) {
			if (formula == null) {
				throw new NullPointerException("formula == null");
			}
			this.formula = formula;
		}

		@Override
		public void setFormula(String formula, Status status, String statusText) {
			setFormula(formula);
			this.status = status;
			this.statusText = statusText;
		}

		public Status getStatus() {
			return status;
		}

		public String getStatusText() {
			return statusText;
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
}
