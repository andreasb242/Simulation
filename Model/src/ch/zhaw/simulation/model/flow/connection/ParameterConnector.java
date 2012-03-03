package ch.zhaw.simulation.model.flow.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.BezierConnectorData;

/**
 * A parameter connector, this is the thin arrow, which allows only the use of
 * the value, but don't change values
 * 
 * @author Andreas Butti
 */
public class ParameterConnector extends Connector<NamedSimulationObject> implements BezierConnectorData {
	/**
	 * The point for our bezier calculation
	 */
	private Point connectorPoint;

	public ParameterConnector(NamedSimulationObject source, NamedSimulationObject target) {
		super(source, target);
	}

	@Override
	public Point getHelperPoint() {
		return connectorPoint;
	}

	public void setHelperPoint(Point connectorPoint) {
		this.connectorPoint = connectorPoint;
	}
}
