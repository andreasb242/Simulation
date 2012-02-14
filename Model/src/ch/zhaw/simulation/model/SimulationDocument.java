package ch.zhaw.simulation.model;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Vector;

import ch.zhaw.simulation.model.connection.Connector;
import ch.zhaw.simulation.model.connection.FlowConnector;
import ch.zhaw.simulation.model.connection.FlowParameterPoint;
import ch.zhaw.simulation.model.connection.ParameterConnector;
import ch.zhaw.simulation.model.simulation.SimulationModel;


public class SimulationDocument {
	private boolean changed;
	private int id = 0;

	private int lastFlowParameterId = 0;

	private Vector<SimulationObject> data = new Vector<SimulationObject>();
	private Vector<Connector<?>> connectors = new Vector<Connector<?>>();

	private Vector<SimulationListener> listener = new Vector<SimulationListener>();

	private HashMap<String, String> metainf = new HashMap<String, String>();

	private SimulationModel simModel = new SimulationModel();

	public SimulationDocument() {
		setSaved();
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

	public void addData(SimulationObject so) {
		if (data.contains(so)) {
			throw new InvalidParameterException("Model contains already this Object: " + so);
		}

		if (so instanceof NamedSimulationObject) {
			if (((NamedSimulationObject) so).getName() == null) {
				String defaultName = ((NamedSimulationObject) so).getDefaultName();
				((NamedSimulationObject) so).setName(defaultName + id);
				id++;
			}
			checkIntegrity((NamedSimulationObject) so);
		}

		data.add(so);
		fireObjectAdded(so);
	}

	private void checkIntegrity(NamedSimulationObject newObject) {
		String searchName = newObject.getName();

		for (SimulationObject d : data) {
			if (d instanceof NamedSimulationObject) {
				if (d == newObject) {
					continue;
				}
				String name = ((NamedSimulationObject) d).getName();
				if (name.equals(searchName)) {
					String newName = getNewNameFor(newObject);
					newObject.setName(newName);
				}
			}
		}
	}

	private String getNewNameFor(NamedSimulationObject newObject) {
		Vector<String> names = new Vector<String>();
		String name = newObject.getName();

		for (SimulationObject d : data) {
			if (d instanceof NamedSimulationObject) {
				String tmp = ((NamedSimulationObject) d).getName();
				if (tmp.startsWith(name)) {
					names.add(tmp);
				}
			}
		}

		for (int i = 2; i < 1000; i++) {
			String tmp = name + "_" + i;
			if (!names.contains(tmp)) {
				return tmp;
			}
		}
		System.err.println("No new name assigned for \"" + name + "\"!");
		return name;
	}

	public NamedSimulationObject getByName(String name) {
		for (SimulationObject o : data) {
			if (o instanceof NamedSimulationObject) {
				if (((NamedSimulationObject) o).getName().equals(name)) {
					return (NamedSimulationObject) o;
				}
			}
		}
		return null;
	}

	public void removeData(SimulationObject o) {
		if (data.remove(o)) {
			fireObjectRemoved(o);
		} else if (o instanceof InfiniteData) {
			fireObjectRemoved(o);
		}
	}

	public SimulationObject[] getData() {
		return data.toArray(new SimulationObject[] {});
	}

	private boolean existsFlowParameterId(String id) {
		for (SimulationObject d : data) {
			if (d instanceof FlowParameterPoint) {
				FlowParameterPoint p = (FlowParameterPoint) d;
				if (id.equals(p.getName())) {
					return true;
				}
			}
		}

		return false;
	}

	private void checkFlowParameterId(FlowParameterPoint pp) {
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

	public void addConnector(Connector<?> c) {
		connectors.add(c);

		if (c instanceof FlowConnector) {
			FlowParameterPoint pp = ((FlowConnector) c).getParameterPoint();
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

	public void clear() {
		for (Connector<?> c : getConnectors()) {
			removeConnector(c);
		}
		connectors.clear();

		for (SimulationObject o : getData()) {
			removeData(o);
		}
		data.clear();

		fireClear();

		setChanged();
		id = 0;
	}

	public void addListener(SimulationListener l) {
		listener.add(l);
	}

	public void removeListener(SimulationListener l) {
		listener.remove(l);
	}

	private void fireClear() {
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).clearData();
		}
	}

	public void fireObjectAdded(SimulationObject o) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataAdded(o);
		}
	}

	public void fireObjectRemoved(SimulationObject o) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataRemoved(o);
		}
	}

	public void fireObjectChanged(SimulationObject o, boolean state) {
		if (!state) {
			setChanged();
		}
		if (o instanceof NamedSimulationObject) {
			checkIntegrity((NamedSimulationObject) o);
		}
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataChanged(o);
		}
	}

	public void fireObjectSaved() {
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataSaved(changed);
		}
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

	public SimulationModel getSimModel() {
		return simModel;
	}

	public void setSaved() {
		if (changed) {
			changed = false;
			fireObjectSaved();
		}
	}

	public void setChanged() {
		if (!changed) {
			changed = true;
			fireObjectSaved();
		}
	}

	public boolean isChanged() {
		return changed;
	}

	public void calculateIds() {
		id = -1;

		for (SimulationObject d : data) {
			if (d instanceof NamedSimulationObject) {
				NamedSimulationObject n = (NamedSimulationObject) d;
				if (n.getName().startsWith("var")) {
					try {
						int i = Integer.parseInt(n.getName().substring(3));
						if (id < i) {
							id = i;
						}
					} catch (Exception e) {
					}
				}
			}
		}

		id++;
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

	public Vector<SimulationGlobal> getSimulationGlobal() {
		Vector<SimulationGlobal> globals = new Vector<SimulationGlobal>();

		for (SimulationObject d : data) {
			if (d instanceof SimulationGlobal) {
				globals.add((SimulationGlobal) d);
			}
		}
		return globals;
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
}
