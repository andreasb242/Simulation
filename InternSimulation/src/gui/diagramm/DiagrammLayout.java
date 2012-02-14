package gui.diagramm;

import java.awt.Color;

public class DiagrammLayout {
	/**
	 * Diagramm margins
	 */
	private int marginLeft = 100;
	private int marginTop = 10;
	private int marginRight = 10;
	private int marginBottom = 10;

	public DiagrammLayout() {
	}

	public int getMarginBottom() {
		return marginBottom;
	}

	public int getMarginLeft() {
		return marginLeft;
	}

	public int getMarginRight() {
		return marginRight;
	}

	public int getMarginTop() {
		return marginTop;
	}

	public Color getCoordinateColor() {
		return Color.BLACK;
	}
}
