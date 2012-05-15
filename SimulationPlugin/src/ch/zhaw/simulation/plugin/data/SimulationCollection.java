package ch.zhaw.simulation.plugin.data;

import java.util.Iterator;
import java.util.Vector;

/**
 * example: capacitor
 * container: Q
 * flow: I
 * parameter: UB, UC, UR, C, R
 * every serie represents one container, flow or parameter
 */
public class SimulationCollection implements Iterable<SimulationSerie> {
	private double startTime;
	private double endTime;

	private Vector<SimulationSerie> series = new Vector<SimulationSerie>();

	public SimulationCollection(double startTime, double endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public SimulationCollection() {
		this.startTime = 0.0;
		this.endTime = 0.0;
	}

	public void addSerie(SimulationSerie serie) {
		series.add(serie);
	}

	public SimulationSerie[] getSeries() {
		return series.toArray(new SimulationSerie[] {});
	}

	public SimulationSerie getSerie(int index) {
		return series.get(index);
	}

	public int size() {
		return series.size();
	}

	public double getStartTime() {
		return startTime;
	}

	public double getEndTime() {
		return endTime;
	}

	@Override
	public Iterator<SimulationSerie> iterator() {
		return series.iterator();
	}
}
