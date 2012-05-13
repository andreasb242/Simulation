package ch.zhaw.simulation.diagram.strokeeditor;

import java.awt.BasicStroke;

import ch.zhaw.simulation.diagram.DiagramStrokeFactory;

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
		return DiagramStrokeFactory.createStroke(this.thikness, this.dash);
	}
}