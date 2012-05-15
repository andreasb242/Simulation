package ch.zhaw.simulation.xyviewer.model;

import ch.zhaw.simulation.plugin.data.SimulationCollection;
import ch.zhaw.simulation.plugin.data.SimulationSerie;

import java.awt.*;
import java.util.HashMap;

public class XYResultEntry {
	private HashMap<Integer, XYResultStepEntry> stepMap = new HashMap<Integer, XYResultStepEntry>();
	private SimulationCollection collection = new SimulationCollection();
	private int colorId;

	private static int colorCounter = 0;

	public XYResultEntry() {
		colorId = colorCounter++;
	}

	public static void resetColorCounter() {
		colorCounter = 0;
	}

	public XYResultStepEntry getStep(int step) {
		return stepMap.get(step);
	}

	public int getColorId() {
		return colorId;
	}

	protected void addStep(XYResultStepEntry entry) {
		stepMap.put(entry.getStep(), entry);
	}

	protected void addSerie(SimulationSerie serie) {
		collection.addSerie(serie);
	}

	public int getStepCount() {
		return stepMap.size();
	}

}
