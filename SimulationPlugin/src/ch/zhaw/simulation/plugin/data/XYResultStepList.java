package ch.zhaw.simulation.plugin.data;

import java.util.Iterator;
import java.util.Vector;

public class XYResultStepList implements Iterable<XYResultStepEntry> {
	private Vector<XYResultStepEntry> stepList = new Vector<XYResultStepEntry>();


	protected void add(XYResultStepEntry stepEntry) {
		stepList.add(stepEntry);
	}

	@Override
	public Iterator<XYResultStepEntry> iterator() {
		return stepList.iterator();
	}
}
