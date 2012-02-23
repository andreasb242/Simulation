package ch.zhaw.simulation.model.flow;


public abstract class NamedSimulationObject extends SimulationObject implements Comparable<NamedSimulationObject> {
	private String name;

	private String formula = "";

	private Status staus = Status.NOT_PARSED;

	private String statusText;

	public SimulationAttachment a;

	public static enum Status {
		NOT_PARSED, SYNTAX_OK, SYNTAX_ERROR
	};

	public NamedSimulationObject(int x, int y) {
		super(x, y);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFormula(String formula) {
		if (formula == null) {
			throw new NullPointerException("formula == null");
		}
		this.formula = formula;
		this.staus = Status.NOT_PARSED;
		this.statusText = null;
	}

	public void setFormula(String formula, Status status, String statusText) {
		if (formula == null) {
			throw new NullPointerException("formula == null");
		}
		this.formula = formula;
		this.staus = status;
		this.statusText = statusText;
	}

	public String getFormula() {
		return formula;
	}

	public void setStaus(Status staus, String statusText) {
		this.staus = staus;
		this.statusText = statusText;
	}

	public String getStatusText() {
		return statusText;
	}

	public Status getStaus() {
		return staus;
	}

	public abstract String getDefaultName();

	@Override
	public int compareTo(NamedSimulationObject o) {
		return name.compareTo(o.name);
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getName();
	}
}
