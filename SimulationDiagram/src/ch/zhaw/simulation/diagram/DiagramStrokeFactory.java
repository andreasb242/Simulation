package ch.zhaw.simulation.diagram;

import java.awt.BasicStroke;

/**
 * This is the base Stroke configuration for the Diagram lines
 * 
 * @author Andreas Butti
 * 
 */
public class DiagramStrokeFactory {

	public static BasicStroke createStroke() {
		return createStroke(null);
	}

	public static BasicStroke createStroke(float[] dash) {
		return createStroke(2, dash);
	}

	public static BasicStroke createStroke(float thikness, float[] dash) {
		// BasicStroke.CAP_SQUARE
		// BasicStroke.CAP_BUTT
		return new BasicStroke(thikness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, dash, 0);
	}
}
