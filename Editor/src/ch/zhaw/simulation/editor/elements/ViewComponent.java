package ch.zhaw.simulation.editor.elements;

import java.awt.Graphics2D;

/**
 * This is an interface for a View element reponds to a model element
 */
public interface ViewComponent {
	// TODO: docu
	public boolean isDependent();

	public void paintShadow(Graphics2D g);
}
