package ch.zhaw.simulation.model;

import java.security.InvalidParameterException;
import java.util.Vector;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.SimulationData;
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
	protected Vector<SimulationData> data = new Vector<SimulationData>();

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
	public void addData(SimulationData so) {
		if (data.contains(so)) {
			throw new InvalidParameterException("Model contains already this Object: " + so);
		}

		if (so instanceof AbstractNamedSimulationData) {
			if (((AbstractNamedSimulationData) so).getName() == null) {
				String defaultName = ((AbstractNamedSimulationData) so).getDefaultName();
				((AbstractNamedSimulationData) so).setName(defaultName + id);
				id++;
			}
			checkIntegrity((AbstractNamedSimulationData) so);
		}

		data.add(so);
		fireObjectAdded(so);
	}

	/**
	 * Checks if this object can be inserted into this model, if not it will be
	 * reanamed
	 */
	private void checkIntegrity(AbstractNamedSimulationData newObject) {
		String searchName = newObject.getName();

		for (SimulationData d : data) {
			if (d instanceof AbstractNamedSimulationData) {
				if (d == newObject) {
					continue;
				}
				String name = ((AbstractNamedSimulationData) d).getName();
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
	private String getNewNameFor(AbstractNamedSimulationData newObject) {
		Vector<String> names = new Vector<String>();
		String name = newObject.getName();

		for (SimulationData d : data) {
			if (d instanceof AbstractNamedSimulationData) {
				String tmp = ((AbstractNamedSimulationData) d).getName();
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
	public AbstractNamedSimulationData getByName(String name) {
		for (SimulationData o : data) {
			if (o instanceof AbstractNamedSimulationData) {
				if (((AbstractNamedSimulationData) o).getName().equals(name)) {
					return (AbstractNamedSimulationData) o;
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
	public Vector<SimulationGlobalData> getGlobalsFor(SimulationData o) {
		Vector<SimulationGlobalData> globals = new Vector<SimulationGlobalData>();
		for (SimulationData d : data) {
			if (d instanceof SimulationGlobalData) {
				globals.add((SimulationGlobalData) d);
			}
		}

		return globals;
	}

	public Vector<AbstractNamedSimulationData> getSource(SimulationData data) {
		return new Vector<AbstractNamedSimulationData>();
	}

	/**
	 * Removes an object from the simulation model
	 * 
	 * @param o
	 *            The object to remove
	 */
	public void removeData(SimulationData o) {
		if (data.remove(o)) {
			fireObjectRemoved(o);
		} else if (o instanceof InfiniteData) {
			fireObjectRemoved(o);
		}
	}

	/**
	 * @return All objects (without connectors)
	 */
	public SimulationData[] getData() {
		return data.toArray(new SimulationData[] {});
	}

	/**
	 * Clears the model
	 */
	public void clear() {
		for (SimulationData o : getData()) {
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

	public void removeListener(SimulationListener l) {
		listener.remove(l);
	}

	protected void fireClear() {
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).clearData();
		}
	}

	public void fireObjectAdded(SimulationData o) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataAdded(o);
		}
	}

	public void fireObjectRemoved(SimulationData o) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataRemoved(o);
		}
	}

	public void fireObjectChanged(SimulationData o, boolean state) {
		if (!state) {
			setChanged();
		}
		if (o instanceof AbstractNamedSimulationData) {
			checkIntegrity((AbstractNamedSimulationData) o);
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

		for (SimulationData d : data) {
			if (d instanceof AbstractNamedSimulationData) {
				AbstractNamedSimulationData n = (AbstractNamedSimulationData) d;
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

	public Vector<SimulationGlobalData> getSimulationGlobal() {
		Vector<SimulationGlobalData> globals = new Vector<SimulationGlobalData>();

		for (SimulationData d : data) {
			if (d instanceof SimulationGlobalData) {
				globals.add((SimulationGlobalData) d);
			}
		}
		return globals;
	}

	/**
	 * @return the type of this model
	 */
	public abstract SimulationType getModelType();
}
