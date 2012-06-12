package ch.zhaw.simulation.editor.util;

import java.awt.Graphics2D;

public class ArrowDraw {
	private int arrowLength;

	public ArrowDraw(int arrowLength) {
		this.arrowLength = arrowLength;
	}

	public void setArrowLength(int arrowLength) {
		this.arrowLength = arrowLength;
	}

	public int getArrowLength() {
		return arrowLength;
	}

	public void drawArrow(Graphics2D g, double lastX1, double lastY1, double lastX2, double lastY2) {
		double x = lastX1 - lastX2;
		double y = lastY1 - lastY2;

		double angle = Math.PI / 8;
		double rad = Math.atan(x / y);

		int arrowLength = this.arrowLength;

		if (y >= 0) {
			arrowLength = -arrowLength;
		}

		x = Math.sin(rad - angle) * arrowLength;
		y = Math.cos(rad - angle) * arrowLength;

		g.drawLine((int) (lastX2 - x), (int) (lastY2 - y), (int) lastX2, (int) lastY2);

		x = Math.sin(rad + angle) * arrowLength;
		y = Math.cos(rad + angle) * arrowLength;

		g.drawLine((int) (lastX2 - x), (int) (lastY2 - y), (int) lastX2, (int) lastY2);
	}

}
