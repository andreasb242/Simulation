package ch.zhaw.simulation.model.connection;

import java.util.Vector;

import ch.zhaw.simulation.model.SimulationObject;


public class FlowConnector extends Connector<SimulationObject> {
	private Vector<ConnectorDeletedListener> listener = new Vector<ConnectorDeletedListener>();
	private FlowValve parameterPoint = new FlowValve(-1, -1);
	
	public FlowConnector(SimulationObject source, SimulationObject target) {
		super(source, target);
	}
	
	public void fireFlowConnectorDeleted() {
		for(int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorDeleted();
		}
	}
	
	public FlowValve getParameterPoint() {
		return parameterPoint;
	}
	
	public void addConnectorDeletedListener(ConnectorDeletedListener l) {
		listener.add(l);
	}
	
	public void removeConnectorDeletedListener(ConnectorDeletedListener l) {
		listener.remove(l);
	}
	
	public interface ConnectorDeletedListener {
		public void connectorDeleted();
	}
	
	@Override
	public String toString() {
		return getClass().getName() + "(flow:" + getParameterPoint().getName() + "): " + connectorString();
	}
}
