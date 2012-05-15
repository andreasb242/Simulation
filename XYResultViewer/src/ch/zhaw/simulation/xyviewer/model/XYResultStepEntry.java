package ch.zhaw.simulation.xyviewer.model;

public class XYResultStepEntry {
	private int step;
	private int x;
	private int y;
	private XYResultEntry resultEntry;

	public XYResultStepEntry() {
		//
	}

	public XYResultStepEntry(int step, int x, int y, XYResultEntry resultEntry) {
		this.step = step;
		this.x = x;
		this.y = y;
		this.resultEntry = resultEntry;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public XYResultEntry getResultEntry() {
		return resultEntry;
	}

	public void setResultEntry(XYResultEntry resultEntry) {
		this.resultEntry = resultEntry;
	}
}
