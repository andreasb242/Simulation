package ch.zhaw.simulation.plugin.data;

import ch.zhaw.simulation.model.xy.ColorCalculator;

import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

public class XYResultList implements Iterable<XYResultEntry> {
	private Vector<XYResultEntry> entryList = new Vector<XYResultEntry>();
	private int stepCount = Integer.MAX_VALUE;
	private Dimension modelSize;
	private int colorCount = 0;

	public XYResultList(int width, int height, int colorCount) {
		setModelSize(width, height);
		this.colorCount = colorCount;
	}

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

	public void add(XYResultEntry resultEntry) {
		entryList.add(resultEntry);

		int stepCount = resultEntry.getStepCount();
		if (this.stepCount > stepCount) {
			this.stepCount = stepCount;
		}
	}

	/**
	 * Get model size, so the components size of the XYViewer (where all mesos
	 * get placed)
	 * 
	 * @return width and height as a Dimension object
	 */
	public Dimension getModelSize() {
		return modelSize;
	}

	/**
	 * Set model size, so the components size of the XYViewer (where all mesos
	 * get placed)
	 * 
	 * @param width
	 * @param height
	 */
	public void setModelSize(int width, int height) {
		modelSize = new Dimension(width, height);
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

	public Color[] getColors() {
		return ColorCalculator.calcColors(this.colorCount);
	}
}
