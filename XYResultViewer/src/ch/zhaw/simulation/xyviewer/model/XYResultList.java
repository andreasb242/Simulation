package ch.zhaw.simulation.xyviewer.model;

import ch.zhaw.simulation.model.xy.ColorCalculator;

import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

public class XYResultList implements Iterable<XYResultEntry> {
	private Vector<XYResultEntry> entryList = new Vector<XYResultEntry>();
	private int stepCount = Integer.MAX_VALUE;
	private Dimension modelSize;

	public XYResultStepList getStep(int step) {
		XYResultStepList stepList = new XYResultStepList();

		for (XYResultEntry entry : this) {
			stepList.add(entry.getStep(step));
		}

		return stepList;
	}

	@Override
	public Iterator<XYResultEntry> iterator() {
		return entryList.iterator();
	}

	protected void add(XYResultEntry resultEntry) {
		entryList.add(resultEntry);

		int stepCount = resultEntry.getStepCount();
		if (this.stepCount > stepCount) {
			this.stepCount = stepCount;
		}
	}

	public Dimension getModelSize() {
		return modelSize;
	}

	protected void setModelSize(int width, int height) {
		modelSize = new Dimension(width, height);
	}

	public Color[] getColors() {
		return ColorCalculator.calcColors(entryList.size());
	}

	public int size() {
		return entryList.size();
	}

	public int getStepCount() {
		if (entryList.size() > 0) {
			return stepCount - 1;
		}
		return 0;
	}
}
