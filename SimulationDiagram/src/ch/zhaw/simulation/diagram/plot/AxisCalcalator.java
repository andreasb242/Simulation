package ch.zhaw.simulation.diagram.plot;

public class AxisCalcalator {

	private double rangeMin;
	private double rangeMax;
	private int lineSpace;

	public void setRange(double rangeMin, double rangeMax, int lineSpace) {
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.lineSpace = lineSpace;
	}

	public int calculateAxisInterval(double zoom) {
		// double width = rangeMax - rangeMin;

		int interval = lineSpace; //(int) (zoom / 100.0 * lineSpace);
		return Math.max(interval, 10);
	}

}
