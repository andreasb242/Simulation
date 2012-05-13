package ch.zhaw.simulation.diagram.strokeeditor;

import java.awt.BasicStroke;

/**
 * @author Andreas Butti
 */
public class StrokData {
	private float thikness = 2.0f;
	private float[] dash = null;

	public StrokData() {
	}

	public float getThikness() {
		return thikness;
	}

	public void setThikness(float thikness) {
		this.thikness = thikness;
	}

	public float[] getDash() {
		return dash;
	}

	public void setDash(float[] dash) {
		this.dash = dash;
	}

	public BasicStroke getStroke() {
		return new BasicStroke(this.thikness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, this.dash, 0);
	}
}