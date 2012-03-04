package ch.zhaw.simulation.model.flow;

import java.awt.Point;

import ch.zhaw.simulation.model.element.AbstractSimulationData;

/**
 * This is a part of an arrow, draw with a Bézier curve, so we need a start, an
 * end and a helper point
 * 
 * @author Andreas Butti
 */
public interface BezierConnectorData {

	/**
	 * @return The Bézier Helper Point
	 */
	public Point getHelperPoint();

	/**
	 * Sets the helper point
	 */
	public void setHelperPoint(Point point);

	/**
	 * Return the source of this connector
	 * 
	 * @return "from"
	 */
	public AbstractSimulationData getSource();

	/**
	 * Returns the target of this connector
	 * 
	 * @return "to"
	 */
	public AbstractSimulationData getTarget();
}
