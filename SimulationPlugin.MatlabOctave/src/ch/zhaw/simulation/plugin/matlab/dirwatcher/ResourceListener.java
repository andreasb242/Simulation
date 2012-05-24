package ch.zhaw.simulation.plugin.matlab.dirwatcher;

/**
 * @author: bachi
 */
public interface ResourceListener<E> {
	public void resourceAdded(E event);
	public void resourceChanged(E event);
	public void resourceDeleted(E event);
}
