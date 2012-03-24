package ch.zhaw.simulation.plugin.data;

import java.util.Vector;

public class SimulationCollection {
	private double ymin;
	private double ymax;

	private double startTime = 0;
	private double endTime;

	private Vector<SimulationSerie> series = new Vector<SimulationSerie>();

	public SimulationCollection(double startTime, double endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void addSeries(SimulationSerie serie) {
		series.add(serie);
	}

	public SimulationSerie[] getSeries() {
		return series.toArray(new SimulationSerie[] {});
	}

	public int size() {
		return series.size();
	}

	public void setYMax(double ymax) {
		this.ymax = ymax;
	}

	public double getYMax() {
		return ymax;
	}

	public void setYMin(double ymin) {
		this.ymin = ymin;
	}

	public double getYMin() {
		return ymin;
	}

	public boolean isColumnNumeric() {
		return true;
	}

	public double getStartTime() {
		return startTime;
	}

	public double getEndTime() {
		return endTime;
	}
}
