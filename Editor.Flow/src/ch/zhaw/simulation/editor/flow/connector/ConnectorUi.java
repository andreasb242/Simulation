package ch.zhaw.simulation.editor.flow.connector;

import java.awt.Graphics2D;
import java.awt.Point;

import ch.zhaw.simulation.editor.imgexport.Paintable;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.selection.SelectableElement;

/**
 * Connector
 * 
 * @author Andreas Butti
 */
public interface ConnectorUi extends Paintable {
	/**
	 * Releases all ressources and removes listener
	 */
	public void dispose();

	/**
	 * Paints the Connector
	 * 
	 * @param g
	 *            The Graphicts to paint on
	 */
	public void paint(Graphics2D g);

	/**
	 * Gets the Data
	 */
	public AbstractConnectorData<?> getData();

	/**
	 * 
	 * @param point
	 *            Die koordinate auf die geklickt wurde
	 * @return true if on the connector
	 */
	public boolean isConnector(Point point);

	/**
	 * Gets the selectable elements
	 */
	public SelectableElement<?>[] getSelectableElements();
}