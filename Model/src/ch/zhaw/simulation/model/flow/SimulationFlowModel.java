package ch.zhaw.simulation.model.flow;

import java.util.Vector;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.SimulationType;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowConnectorData;
import ch.zhaw.simulation.model.flow.connection.FlowValveData;
import ch.zhaw.simulation.model.flow.connection.ParameterConnectorData;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;
import ch.zhaw.simulation.model.listener.FlowSimulationAdapter;
import ch.zhaw.simulation.model.listener.FlowSimulationListener;
import ch.zhaw.simulation.model.listener.SimulationListener;

/**
 * This is the model for Flow Simulations
 * 
 * @author Andreas Butti
 */
public class SimulationFlowModel extends AbstractSimulationModel<FlowSimulationListener> {

	/**
	 * Used to calculate the next id
	 */
	private int lastFlowParameterId = 0;
	private Vector<AbstractConnectorData<?>> connectors = new Vector<AbstractConnectorData<?>>();

	public SimulationFlowModel() {
	}

	private boolean existsFlowParameterId(String id) {
		for (AbstractSimulationData d : data) {
			if (d instanceof FlowValveData) {
				FlowValveData p = (FlowValveData) d;
				if (id.equals(p.getName())) {
					return true;
				}
			}
		}

		return false;
	}

	private void checkFlowParameterId(FlowValveData pp) {
		// if there is already a name assigned
		if (pp.getName() != null) {
			// check if the name not exists
			if (!existsFlowParameterId(pp.getName())) {
				// the name doesent exist
				return;
			}
		}

		// assigne a new id
		while (existsFlowParameterId(String.valueOf("fluss" + lastFlowParameterId))) {
			lastFlowParameterId++;
		}
		pp.setName(String.valueOf("fluss" + lastFlowParameterId));
	}

	@Override
	public void clear() {
		for (AbstractConnectorData<?> c : getConnectors()) {
			removeConnector(c);
		}
		connectors.clear();

		super.clear();
	}

	public void addConnector(AbstractConnectorData<?> c) {
		connectors.add(c);

		if (c instanceof FlowConnectorData) {
			FlowValveData pp = ((FlowConnectorData) c).getValve();
			checkFlowParameterId(pp);
			data.add(pp);
		}
		fireConnectorAdded(c);
	}

	public void removeConnector(AbstractConnectorData<?> c) {
		connectors.remove(c);
		fireConnectorRemoved(c);
	}

	public AbstractConnectorData<?>[] getConnectors() {
		return connectors.toArray(new AbstractConnectorData<?>[] {});
	}

	public void fireConnectorAdded(AbstractConnectorData<?> c) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorAdded(c);
		}
	}

	public void fireConnectorRemoved(AbstractConnectorData<?> c) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorRemoved(c);
		}
	}

	public void fireConnectorChanged(AbstractConnectorData<?> c) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorChanged(c);
		}
	}

	@Override
	public SimulationType getModelType() {
		return SimulationType.FLOW_SIMULATION;
	}

	@Override
	public Vector<SimulationGlobalData> getGlobalsFor(AbstractSimulationData o) {
		Vector<SimulationGlobalData> globals = new Vector<SimulationGlobalData>();
		for (AbstractSimulationData d : data) {
			if (d instanceof SimulationGlobalData && !checkDependency(d, o)) {
				globals.add((SimulationGlobalData) d);
			}
		}

		return globals;
	}

	/**
	 * Prüft eine Abhängigkeit vom aktuellen Objekt zu o
	 * 
	 * @param o
	 * @return true wenn eine Abhängigkeit vorhanden ist
	 */
	private boolean checkDependency(AbstractSimulationData o, AbstractSimulationData to) {
		for (AbstractConnectorData<?> c : getConnectorsTo(o)) {
			if (c.getSource() == to) {
				// Beziehung vorhanden: Globale ist Abhängig von "o", somit kann
				// in "o" die globale nicht verwendet werden
				return true;
			} else if (c.getTarget() == o && checkDependency(c.getSource(), to)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasFlowConnectors(SimulationContainerData container) {
		for (AbstractConnectorData<?> c : connectors) {
			if (c instanceof FlowConnectorData) {
				FlowConnectorData flow = (FlowConnectorData) c;
				if (flow.getSource() == container || flow.getTarget() == container) {
					return true;
				}
			}
		}

		return false;
	}

	public Vector<FlowConnectorData> getFlowConnectors() {
		Vector<FlowConnectorData> flows = new Vector<FlowConnectorData>();
		for (AbstractConnectorData<?> c : connectors) {
			if (c instanceof FlowConnectorData) {
				flows.add((FlowConnectorData) c);
			}
		}

		return flows;
	}

	@Override
	public Vector<AbstractNamedSimulationData> getSource(AbstractSimulationData data) {
		Vector<AbstractNamedSimulationData> source = new Vector<AbstractNamedSimulationData>();
		for (AbstractConnectorData<?> c : connectors) {
			if (c instanceof ParameterConnectorData && c.getTarget() == data && c.getSource() instanceof AbstractNamedSimulationData) {
				source.add((AbstractNamedSimulationData) c.getSource());
			}
		}

		return source;
	}

	public Vector<AbstractConnectorData<?>> getConnectorsTo(AbstractSimulationData data) {
		Vector<AbstractConnectorData<?>> connectors = new Vector<AbstractConnectorData<?>>();
		for (AbstractConnectorData<?> c : this.connectors) {
			if (c.getTarget() == data || c.getSource() == data) {
				connectors.add(c);
				if (c instanceof FlowConnectorData) {
					getDeepConnectorsTo(connectors, ((FlowConnectorData) c).getValve());
				}
			}
		}

		return connectors;
	}

	private void getDeepConnectorsTo(Vector<AbstractConnectorData<?>> connectors, FlowValveData flowValve) {
		for (AbstractConnectorData<?> c : this.connectors) {
			if (c.getTarget() == flowValve || c.getSource() == flowValve) {
				connectors.add(c);
			}
		}
	}

	public Vector<ParameterConnectorData> getParameterConnectorsTo(AbstractSimulationData data) {
		Vector<ParameterConnectorData> connectors = new Vector<ParameterConnectorData>();
		for (AbstractConnectorData<?> c : this.connectors) {
			if (c instanceof ParameterConnectorData && (c.getTarget() == data || c.getSource() == data)) {
				connectors.add((ParameterConnectorData) c);
			}
		}

		return connectors;
	}

	public Vector<SimulationContainerData> getSimulationContainer() {
		Vector<SimulationContainerData> containers = new Vector<SimulationContainerData>();

		for (AbstractSimulationData d : data) {
			if (d instanceof SimulationContainerData) {
				containers.add((SimulationContainerData) d);
			}
		}
		return containers;
	}

	public Vector<SimulationParameterData> getSimulationParameter() {
		Vector<SimulationParameterData> parameteres = new Vector<SimulationParameterData>();

		for (AbstractSimulationData d : data) {
			if (d instanceof SimulationParameterData) {
				parameteres.add((SimulationParameterData) d);
			}
		}
		return parameteres;
	}

	public Vector<AbstractNamedSimulationData> getNamedSimulationObject() {
		Vector<AbstractNamedSimulationData> containers = new Vector<AbstractNamedSimulationData>();

		for (AbstractSimulationData d : data) {
			if (d instanceof AbstractNamedSimulationData) {
				containers.add((AbstractNamedSimulationData) d);
			}
		}

		return containers;
	}

	public boolean containsContainerOrParameter() {
		for (AbstractSimulationData d : data) {
			if (d instanceof SimulationContainerData) {
				return true;
			}
			if (d instanceof SimulationParameterData) {
				return true;
			}
		}

		return false;
	}

	@Override
	public FlowSimulationListener addSimulationListener(final SimulationListener l) {
		FlowSimulationAdapter adapter = new FlowSimulationAdapter() {

			@Override
			public void dataAdded(AbstractSimulationData o) {
				l.dataAdded(o);
			}

			@Override
			public void dataRemoved(AbstractSimulationData o) {
				l.dataRemoved(o);
			}

			@Override
			public void dataChanged(AbstractSimulationData o) {
				l.dataChanged(o);
			}

			@Override
			public void dataSaved(boolean saved) {
				l.dataSaved(saved);
			}

			@Override
			public void clearData() {
				l.clearData();
			}
		};

		addListener(adapter);

		return adapter;
	}

}
