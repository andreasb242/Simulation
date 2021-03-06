package ch.zhaw.simulation.model.selection;

/**
 * Interface for selectable (gui) elements
 * 
 * @author Andreas Butti
 */
public interface SelectableElement<T> {
	/**
	 * The X pos (left)
	 */
	public int getX();

	/**
	 * The Y pos (top)
	 */
	public int getY();

	/**
	 * The width
	 */
	public int getWidth();

	/**
	 * The height
	 */
	public int getHeight();

	/**
	 * Moves this element
	 * 
	 * @param dX
	 *            Delta X
	 * @param dY
	 *            Delta Y
	 */
	public void moveElement(int dX, int dY);
	
	/**
	 * @return The data of this view, or <code>null</code> if no data is available
	 */
	public T getData();
}
