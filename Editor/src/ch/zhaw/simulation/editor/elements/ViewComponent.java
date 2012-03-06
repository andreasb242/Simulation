package ch.zhaw.simulation.editor.elements;

import java.awt.Graphics2D;

/**
 * This is an interface for a View element reponds to a model element
 */
public interface ViewComponent {

	/**
	 * @return true if this element dependents on the currently selected element
	 */
	public boolean isDependent();

	/**
	 * Draws the dependency shadow
	 */
	public void paintShadow(Graphics2D g);
}
