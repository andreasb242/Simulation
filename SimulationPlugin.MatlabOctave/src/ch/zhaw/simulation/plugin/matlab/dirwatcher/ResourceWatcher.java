package ch.zhaw.simulation.plugin.matlab.dirwatcher;

import java.util.Collection;
import java.util.Vector;

/**
 * @author: bachi
 */
public abstract class ResourceWatcher<E extends ResourceListener<F>, F> extends IntervalThread {
	private Collection<E> listeners;

	public ResourceWatcher(int interval) {
		super(interval);
		listeners = new Vector<E>();
	}

	public void addResourceListener(E listener) {
		listeners.add(listener);
	}

	public void removeAllResourceListeners() {
		listeners.clear();
	}

	protected void resourceAdded(F event) {
		for (E listener : listeners) {
			listener.resourceAdded(event);
		}
	}

	protected void resourceChanged(F event) {
		for (E listener : listeners) {
			listener.resourceChanged(event);
		}
	}

	protected void resourceDeleted(F event) {
		for (E listener : listeners) {
			listener.resourceDeleted(event);
		}
	}

}
