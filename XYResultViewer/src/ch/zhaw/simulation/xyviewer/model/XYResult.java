package ch.zhaw.simulation.xyviewer.model;

import java.awt.Color;
import java.awt.Dimension;

public interface XYResult {

	/**
	 * @return The result count, if stepsize is 0.1 and range is from 0 to 1 it
	 *         should return 10
	 */
	public int getCount();

	/**
	 * 
	 * @param n
	 *            The step
	 * @return The nth step
	 */
	public XYResultStep getStep(int n);

	/**
	 * @return The size of the XY-Model in pixels
	 */
	public Dimension getModelSize();

	/**
	 * 
	 * @return The colors for the meso compartments
	 */
	public Color[] getColors();

}
