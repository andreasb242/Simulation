package ch.zhaw.simulation.model.flow;

import java.util.HashMap;
import java.util.Vector;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.connection.FlowConnector;
import ch.zhaw.simulation.model.flow.connection.FlowValve;
import ch.zhaw.simulation.model.flow.connection.ParameterConnector;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;
import ch.zhaw.simulation.model.flow.element.SimulationParameter;
import ch.zhaw.simulation.model.flow.simulation.SimulationConfiguration;
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
	private Vector<Connector<?>> connectors = new Vector<Connector<?>>();

	private HashMap<String, String> metainf = new HashMap<String, String>();

	private SimulationConfiguration simModel = new SimulationConfiguration();

	public SimulationFlowModel() {
	}

	public void clearMetadata() {
		metainf.clear();
	}

	public void putMetainf(String key, String value) {
		metainf.put(key, value);
	}

	public String getMetainf(String key) {
		return metainf.get(key);
	}

	public String[] getMetainfKeys() {
		return metainf.keySet().toArray(new String[] {});
	}

	private boolean existsFlowParameterId(String id) {
		for (SimulationObject d : data) {
			if (d instanceof FlowValve) {
				FlowValve p = (FlowValve) d;
				if (id.equals(p.getName())) {
					return true;
				}
			}
		}

		return false;
	}

	private void checkFlowParameterId(FlowValve pp) {
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
		for (Connector<?> c : getConnectors()) {
			removeConnector(c);
		}
		connectors.clear();

		super.clear();
	}

	public void addConnector(Connector<?> c) {
		connectors.add(c);

		if (c instanceof FlowConnector) {
			FlowValve pp = ((FlowConnector) c).getValve();
			checkFlowParameterId(pp);
			data.add(pp);
		}
		fireConnectorAdded(c);
	}

	public void removeConnector(Connector<?> c) {
		connectors.remove(c);
		fireConnectorRemoved(c);
	}

	public Connector<?>[] getConnectors() {
		return connectors.toArray(new Connector<?>[] {});
	}

	public void fireConnectorAdded(Connector<?> c) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorAdded(c);
		}
	}

	public void fireConnectorRemoved(Connector<?> c) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorRemoved(c);
		}
	}

	public void fireConnectorChanged(Connector<?> c) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).connectorChanged(c);
		}
	}

	public SimulationConfiguration getSimulationConfiguration() {
		return simModel;
	}

	@Override
	public boolean isFlowModel() {
		return true;
	}

	@Override
	public Vector<SimulationGlobal> getGlobalsFor(SimulationObject o) {
		Vector<SimulationGlobal> globals = new Vector<SimulationGlobal>();
		for (SimulationObject d : data) {
			if (d instanceof SimulationGlobal && !checkDependency(d, o)) {
				globals.add((SimulationGlobal) d);
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
	private boolean checkDependency(SimulationObject o, SimulationObject to) {
		for (Connector<?> c : getConnectorsTo(o)) {
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

	public boolean hasFlowConnectors(SimulationContainer container) {
		for (Connector<?> c : connectors) {
			if (c instanceof FlowConnector) {
				FlowConnector flow = (FlowConnector) c;
				if (flow.getSource() == container || flow.getTarget() == container) {
					return true;
				}
			}
		}

		return false;
	}

	public Vector<FlowConnector> getFlowConnectors() {
		Vector<FlowConnector> flows = new Vector<FlowConnector>();
		for (Connector<?> c : connectors) {
			if (c instanceof FlowConnector) {
				flows.add((FlowConnector) c);
			}
		}

		return flows;
	}

	@Override
	public Vector<NamedSimulationObject> getSource(SimulationObject data) {
		Vector<NamedSimulationObject> source = new Vector<NamedSimulationObject>();
		for (Connector<?> c : connectors) {
			if (c instanceof ParameterConnector && c.getTarget() == data && c.getSource() instanceof NamedSimulationObject) {
				source.add((NamedSimulationObject) c.getSource());
			}
		}

		return source;
	}

	public Vector<Connector<?>> getConnectorsTo(SimulationObject data) {
		Vector<Connector<?>> connectors = new Vector<Connector<?>>();
		for (Connector<?> c : this.connectors) {
			if (c.getTarget() == data || c.getSource() == data) {
				connectors.add(c);
			}
		}

		return connectors;
	}

	public Vector<ParameterConnector> getParameterConnectorsTo(SimulationObject data) {
		Vector<ParameterConnector> connectors = new Vector<ParameterConnector>();
		for (Connector<?> c : this.connectors) {
			if (c instanceof ParameterConnector && (c.getTarget() == data || c.getSource() == data)) {
				connectors.add((ParameterConnector) c);
			}
		}

		return connectors;
	}

	public Vector<SimulationContainer> getSimulationContainer() {
		Vector<SimulationContainer> containers = new Vector<SimulationContainer>();

		for (SimulationObject d : data) {
			if (d instanceof SimulationContainer) {
				containers.add((SimulationContainer) d);
			}
		}
		return containers;
	}

	public Vector<SimulationParameter> getSimulationParameter() {
		Vector<SimulationParameter> parameteres = new Vector<SimulationParameter>();

		for (SimulationObject d : data) {
			if (d instanceof SimulationParameter) {
				parameteres.add((SimulationParameter) d);
			}
		}
		return parameteres;
	}

	public Vector<NamedSimulationObject> getNamedSimulationObject() {
		Vector<NamedSimulationObject> containers = new Vector<NamedSimulationObject>();

		for (SimulationObject d : data) {
			if (d instanceof NamedSimulationObject) {
				containers.add((NamedSimulationObject) d);
			}
		}

		return containers;
	}

	public boolean containsContainerOrParameter() {
		for (SimulationObject d : data) {
			if (d instanceof SimulationContainer) {
				return true;
			}
			if (d instanceof SimulationParameter) {
				return true;
			}
		}

		return false;
	}

	@Override
	public FlowSimulationListener addSimulationListener(final SimulationListener l) {
		FlowSimulationAdapter adapter = new FlowSimulationAdapter() {

			@Override
			public void dataAdded(SimulationObject o) {
				l.dataAdded(o);
			}
			
			@Override
			public void dataRemoved(SimulationObject o) {
				l.dataRemoved(o);
			}
			
			@Override
			public void dataChanged(SimulationObject o) {
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
