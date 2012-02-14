package ch.zhaw.simulation.editor.connector.flowarrow;

/**
 * A direction
 * 
 * @author Andreas Butti
 */
enum Direction {
	TOP(false), RIGHT(true), BOTTOM(false), LEFT(true);
	
	/**
	 * If the direction is horizontal
	 */
	private boolean horizontal;
	
	private Direction(boolean horizontal) {
		this.horizontal = horizontal;
	}
	
	/**
	 * @return true if RIGHT, LEFT
	 */
	public boolean isHorizontal() {
		return horizontal;
	}
}