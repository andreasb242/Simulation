package ch.zhaw.simulation.plugin.matlab.sidebar;

/**
 * @author: bachi
 */
public enum NumericMethodType {
	EULER("Euler"),
	RK4("Runge-Kutta 4"),
	FEHLBERG("Fehlberg"),
	CASH_KARP("Cash–Karp"),
	DORMAND_PRINCE("Dormand–Prince");

	public final String name;

	private NumericMethodType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
