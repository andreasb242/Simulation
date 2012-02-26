package ch.zhaw.simulation.model.flow.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.element.NamedSimulationObject;

/**
 * A parameter connector, this is the thin arrow, wich allows only the use of
 * the value, but don't change values
 * 
 * @author Andreas Butti
 */
public class ParameterConnector extends Connector<NamedSimulationObject> {
	/**
	 * The point for our bezier calculation
	 */
	private Point connectorPoint;

	public ParameterConnector(NamedSimulationObject source, NamedSimulationObject target) {
		super(source, target);
	}

	public Point getConnectorPoint() {
		return connectorPoint;
	}

	public void setConnectorPoint(Point connectorPoint) {
		this.connectorPoint = connectorPoint;
	}
}
