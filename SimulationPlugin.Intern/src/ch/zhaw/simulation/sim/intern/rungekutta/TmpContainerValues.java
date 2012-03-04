package ch.zhaw.simulation.sim.intern.rungekutta;

import ch.zhaw.simulation.sim.intern.rungekutta.RungeKuttaSimulation.FlowType;

public class TmpContainerValues implements TmpValue {
	Object value;

	Object value1;
	Object value2;
	Object value3;
	Object value4;

	public void set(Object value, FlowType savePosition) {
		if (savePosition == FlowType.TMP1) {
			value1 = value;
		} else if (savePosition == FlowType.TMP2) {
			value2 = value;
		} else if (savePosition == FlowType.TMP3) {
			value3 = value;
		} else if (savePosition == FlowType.TMP4) {
			value4 = value;
		} else {
			throw new RuntimeException("Unknown savePosition: " + savePosition);
		}
	}

	public Object getValue(FlowType src) {
		if (src == FlowType.TMP1) {
			return value1;
		} else if (src == FlowType.TMP2) {
			return value2;
		} else if (src == FlowType.TMP3) {
			return value3;
		} else if (src == FlowType.TMP4) {
			return value4;
		} else {
			throw new RuntimeException("Unknown Position: " + src);
		}
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "TmpContainerValues: value1 = " + value1 + ", value2 = " + value2 + ", value3 = " + value3 + ", value4 = " + value4;
	}
}
