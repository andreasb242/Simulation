package ch.zhaw.simulation.diagram;

import java.awt.Color;

public class DiagramConfiguration {
	private Color backgroundColor = Color.WHITE;
	private Color borderColor = Color.BLACK;
	private Color rasterColor = Color.GRAY;

	public DiagramConfiguration() {
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public Color getRasterColor() {
		return rasterColor;
	}
}
