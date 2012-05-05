package ch.zhaw.simulation.inexport.madonna;

import java.awt.Point;

/**
 * Baseclass for all Madonna elements
 * 
 * @author Andreas Butti
 */
public abstract class MadonnaElement {
	/**
	 * The position on the screen
	 */
	private Point pos;

	public MadonnaElement(Point pos) {
		this.pos = pos;
	}

	public Point getPos() {
		return pos;
	}
}
