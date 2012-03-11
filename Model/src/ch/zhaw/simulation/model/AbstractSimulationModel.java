package ch.zhaw.simulation.model;

import java.security.InvalidParameterException;
import java.util.Vector;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
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
	protected Vector<AbstractSimulationData> data = new Vector<AbstractSimulationData>();

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
	public void addData(AbstractSimulationData so) {
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
	protected void checkIntegrity(AbstractNamedSimulationData newObject) {
		String searchName = newObject.getName();

		for (AbstractSimulationData d : data) {
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

		for (AbstractSimulationData d : data) {
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
		for (AbstractSimulationData o : data) {
			if (o instanceof AbstractNamedSimulationData) {
				if (((AbstractNamedSimulationData) o).getName().equals(name)) {
					return (AbstractNamedSimulationData) o;
				}
			}
		}
		return null;
	}

	/**
	 * Returns all globals for <code>data</code> which not depend on it
	 * 
	 * @return The global objects
	 */
	public Vector<SimulationGlobalData> getGlobalsFor(NamedFormulaData data) {
		Vector<SimulationGlobalData> globals = new Vector<SimulationGlobalData>();
		for (AbstractSimulationData d : this.data) {
			if (d instanceof SimulationGlobalData) {
				globals.add((SimulationGlobalData) d);
			}
		}

		return globals;
	}

	public abstract Vector<AbstractNamedSimulationData> getSource(NamedFormulaData data);

	/**
	 * Removes an object from the simulation model
	 * 
	 * @param o
	 *            The object to remove
	 */
	public void removeData(AbstractSimulationData o) {
		if (data.remove(o)) {
			fireObjectRemoved(o);
		} else if (o instanceof InfiniteData) {
			fireObjectRemoved(o);
		}
	}

	/**
	 * @return All objects (without connectors)
	 */
	public AbstractSimulationData[] getData() {
		return data.toArray(new AbstractSimulationData[] {});
	}

	/**
	 * Clears the model
	 */
	public void clear() {
		for (AbstractSimulationData o : getData()) {
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

	public void fireObjectAdded(AbstractSimulationData o) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataAdded(o);
		}
	}

	public void fireObjectRemoved(AbstractSimulationData o) {
		setChanged();
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataRemoved(o);
		}
	}

	public void fireObjectChanged(AbstractSimulationData o) {
		setChanged();
		fireObjectChangedAutoparser(o);
	}

	/**
	 * Autoparser don't change the element, so the model is safed after if it
	 * was it before
	 * 
	 * @param o
	 */
	public void fireObjectChangedAutoparser(Object o) {
		if (o instanceof AbstractNamedSimulationData) {
			checkIntegrity((AbstractNamedSimulationData) o);
		}
		for (int i = 0; i < listener.size(); i++) {
			listener.get(i).dataChanged((AbstractSimulationData)o);
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

		for (AbstractSimulationData d : data) {
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

		for (AbstractSimulationData d : data) {
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
