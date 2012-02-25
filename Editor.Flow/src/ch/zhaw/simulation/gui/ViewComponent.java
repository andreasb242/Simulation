package ch.zhaw.simulation.gui;

import java.awt.Graphics2D;

/**
 * Ein Element aus dem View, das zum Dokument ghört
 */
public interface ViewComponent {
	public boolean isDependent();

	public void paintShadow(Graphics2D g);
}
