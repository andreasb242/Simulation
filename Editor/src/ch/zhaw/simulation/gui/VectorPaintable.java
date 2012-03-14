package ch.zhaw.simulation.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface VectorPaintable {
	public void paintVector(Graphics2D g, boolean onlySelection);

	public void paint(Graphics g);

	/**
	 * @return The current selected rectangle
	 */
	public Rectangle getSelectionRange();
	
	public Dimension getSize();
}
