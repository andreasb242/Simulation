package ch.zhaw.simulation.model.selection;

/**
 * Selection listener
 * 
 * @author Andreas Butti
 */
public interface SelectionListener {

	/**
	 * The selection has changed
	 */
	public void selectionChanged();

	/**
	 * The selection was moved
	 * 
	 * @param dX
	 *            Delta X
	 * @param dY
	 *            Delta Y
	 */
	public void selectionMoved(int dX, int dY);
}
