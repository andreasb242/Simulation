package gui.diagramm;

public class Axis {
	/**
	 * Die Länge in px
	 */
	private int length;

	/**
	 * Der Minimalwert in User-Units
	 */
	private double min;

	/**
	 * Der Maximalwert in User-Units
	 */
	private double max;

	/**
	 * Abstand links oder oben
	 */
	private int padding1;

	/**
	 * Abstand rechts oder unten
	 */
	private int padding2;

	public Axis() {
	}

	public Axis(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public double getZoomFactor() {
		return (max - min) / (length - padding1 - padding2);
	}

	/**
	 * Berechnet die Position eines Punktes vom Model auf dem Diagramm
	 * 
	 * @param value
	 *            Der Wert
	 * @return Der Punkt auf dem Diagramm
	 */
	public double calcPointFromModel(double value) {
		return (value - min) / getZoomFactor();
	}

	public void setPadding1(int padding1) {
		this.padding1 = padding1;
	}

	public void setPadding2(int padding2) {
		this.padding2 = padding2;
	}

	public int getPadding1() {
		return padding1;
	}

	public int getPadding2() {
		return padding2;
	}

	public int getLength() {
		return length;
	}

	public void setMinMax(double min, double max) {
		this.min = min;
		this.max = max;
	}
}
