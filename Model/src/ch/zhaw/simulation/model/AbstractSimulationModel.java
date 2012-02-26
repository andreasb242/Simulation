package ch.zhaw.simulation.model;

import java.security.InvalidParameterException;
import java.util.Vector;

import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;
import ch.zhaw.simulation.model.element.SimulationObject;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.model.listener.SimulationListener;

public abstract class AbstractSimulationModel<T extends SimulationListener> {
	/**
	 * If the model has changed (saved / not saved)
	 */
	private boolean changed;

	/**
	 * The ID is used for creating unique IDs
	 */
	private int id = 0;

	/**
	 * The simulation objects
	 */
	protected Vector<SimulationObject> data = new Vector<SimulationObject>();

	/**
	 * The listeners
	 */
	protected Vector<T> listener = new Vector<T>();

	/**
	 * CTor
	 */
	public AbstractSimulationModel() {
		setSaved();
	}

	/**
	 * Adds a simulation object to the model
	 */
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

	/**
	 * Checks if this object can be inserted into this model, if not it will be
	 * reanamed
	 */
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

	/**
	 * Returns a new name of for this object
	 * 
	 * @param newObject
	 *            The object
	 * @return The new name
	 */
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

	/**
	 * Searches for an object by this name
	 * 
	 * @param name
	 *            The name to search for
	 * @return The object or <code>null</code> if not found
	 */
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

	/**
	 * Returns all globals used by the object <code>o</code>
	 * 
	 * @return The global objects
	 */
	public Vector<SimulationGlobal> getGlobalsFor(SimulationObject o) {
		Vector<SimulationGlobal> globals = new Vector<SimulationGlobal>();
		for (SimulationObject d : data) {
			if (d instanceof SimulationGlobal) {
				globals.add((SimulationGlobal) d);
			}
		}

		return globals;
	}
	
	public Vector<NamedSimulationObject> getSource(SimulationObject data) {
		return new Vector<NamedSimulationObject>();
	}

	/**
	 * Removes an object from the simulation model
	 * 
	 * @param o
	 *            The object to remove
	 */
	public void removeData(SimulationObject o) {
		if (data.remove(o)) {
			fireObjectRemoved(o);
		} else if (o instanceof InfiniteData) {
			fireObjectRemoved(o);
		}
	}

	/**
	 * @return All objects (without connectors)
	 */
	public SimulationObject[] getData() {
		return data.toArray(new SimulationObject[] {});
	}

	/**
	 * Clears the model
	 */
	public void clear() {
		for (SimulationObject o : getData()) {
			removeData(o);
		}
		data.clear();

		fireClear();

		setChanged();
		id = 0;
	}

	public abstract T addSimulationListener(SimulationListener l);

	public void addListener(T l) {
		listener.add(l);
	}

	public void removeListener(T l) {
		listener.remove(l);
	}

	protected void fireClear() {
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

	public Vector<SimulationGlobal> getSimulationGlobal() {
		Vector<SimulationGlobal> globals = new Vector<SimulationGlobal>();

		for (SimulationObject d : data) {
			if (d instanceof SimulationGlobal) {
				globals.add((SimulationGlobal) d);
			}
		}
		return globals;
	}

	/**
	 * @return true if this a flow model, false if this a XY model
	 */
	public abstract boolean isFlowModel();
}
