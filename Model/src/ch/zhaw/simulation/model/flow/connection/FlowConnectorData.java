package ch.zhaw.simulation.model.flow.connection;

import java.util.Vector;

import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.flow.BezierConnectorData;

/**
 * A Flow connction between two Containers, or a Container and an InfiniteData
 * 
 * @author Andreas Butti
 */
public class FlowConnectorData extends AbstractConnectorData<AbstractSimulationData> {
	/**
	 * The listeners for this Connector
	 */
	private Vector<ConnectorDeletedListener> listener = new Vector<ConnectorDeletedListener>();

	/**
	 * The valve of this flow
	 */
	private FlowValveData valve = new FlowValveData(-1, -1);

	
	private BezierConnectorData bezier1 = new FlowConnectorBezierData() {

		@Override
		public AbstractSimulationData getTarget() {
			return valve;
		}

		@Override
		public AbstractSimulationData getSource() {
			return FlowConnectorData.this.getSource();
		}
	};

	private BezierConnectorData bezier2 = new FlowConnectorBezierData() {

		public AbstractSimulationData getTarget() {
			return FlowConnectorData.this.getTarget();
		}

		@Override
		public AbstractSimulationData getSource() {
			return valve;
		}
	};

	public FlowConnectorData(AbstractSimulationData source, AbstractSimulationData target) {
		super(source, target);
	}

	/**
	 * Informs all listenet that this connector was deleted
	 */
	public void fireFlowConnectorDeleted() {
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorDeleted();
		}
	}

	/**
	 * @return The valve
	 */
	public FlowValveData getValve() {
		return valve;
	}

	/**
	 * Interface for Listeners
	 */
	public interface ConnectorDeletedListener {
		public void connectorDeleted();
	}

	/**
	 * Adds a listener
	 */
	public void addConnectorDeletedListener(ConnectorDeletedListener l) {
		listener.add(l);
	}

	/**
	 * Removes a listener
	 */
	public void removeConnectorDeletedListener(ConnectorDeletedListener l) {
		listener.remove(l);
	}

	@Override
	public String toString() {
		return getClass().getName() + "(flow:" + getValve().getName() + "): " + connectorString();
	}

	public BezierConnectorData getBezierSource() {
		return bezier1;
	}

	public BezierConnectorData getBezierTarget() {
		return bezier2;
	}
}
