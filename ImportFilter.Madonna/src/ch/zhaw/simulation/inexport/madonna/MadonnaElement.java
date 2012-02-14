package ch.zhaw.simulation.inexport.madonna;

import java.awt.Point;

public abstract class MadonnaElement {
	private Point pos;

	public MadonnaElement(Point pos) {
		this.pos = pos;
	}

	public Point getPos() {
		return pos;
	}
}
