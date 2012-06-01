package ch.zhaw.simulation.plugin.data;

import java.util.HashMap;

/**
 * This class represents one meso compartiment from a xy-model stepMap has
 * entries of [time, x/y] collection has all submodels of one meso
 */
public class XYResultEntry {
	// stepMap has entries of [time, x/y]
	private HashMap<Integer, XYResultStepEntry> stepMap = new HashMap<Integer, XYResultStepEntry>();

	// collection has all submodels of one meso
	private SimulationCollection collection = new SimulationCollection();

	private int colorId;

	public XYResultEntry(int colorId) {
		this.colorId = colorId;
	}

	public XYResultStepEntry getStep(int step) {
		return stepMap.get(step);
	}

	public int getColorId() {
		return colorId;
	}

	public void addStep(XYResultStepEntry entry) {
		stepMap.put(entry.getStep(), entry);
	}

	public void addSerie(SimulationSerie serie) {
		collection.addSerie(serie);
	}

	public int getStepCount() {
		return stepMap.size();
	}

}
