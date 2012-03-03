package ch.zhaw.simulation.model.flow.connection;

import java.awt.Point;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.BezierConnectorData;

/**
 * A parameter connector, this is the thin arrow, which allows only the use of
 * the value, but don't change values
 * 
 * @author Andreas Butti
 */
public class ParameterConnectorData extends AbstractConnectorData<AbstractNamedSimulationData> implements BezierConnectorData {
	/**
	 * The point for our bezier calculation
	 */
	private Point connectorPoint;

	public ParameterConnectorData(AbstractNamedSimulationData source, AbstractNamedSimulationData target) {
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
