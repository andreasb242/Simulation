package ch.zhaw.simulation.model.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.NamedSimulationObject;


public class ParameterConnector extends Connector<NamedSimulationObject> {

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
