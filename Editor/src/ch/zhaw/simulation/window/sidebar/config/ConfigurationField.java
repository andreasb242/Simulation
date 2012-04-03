package ch.zhaw.simulation.window.sidebar.config;

import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.window.sidebar.config.SidebarActionListener.SidebarAction;

/**
 * A configuration filed within the sidebar
 * 
 * @author Andreas Butti
 * 
 */
public abstract class ConfigurationField implements Comparable<ConfigurationField> {
	/**
	 * Listener to fire events
	 */
	private Vector<SidebarActionListener> listener = new Vector<SidebarActionListener>();

	/**
	 * All components in this fields, for show / hide
	 */
	private Vector<JComponent> components = new Vector<JComponent>();

	/**
	 * Ctor
	 */
	public ConfigurationField() {
	}

	/**
	 * @return The order number, so the fileds are always in the same order
	 */
	public abstract int getOrder();

	/**
	 * If this filed supports multiple editing (e.g. the name filed does not)
	 * 
	 * @return true if yes
	 */
	public abstract boolean supportsMultibleEditing();

	@Override
	public int compareTo(ConfigurationField o) {
		return getOrder() - o.getOrder();
	}

	/**
	 * Initializes the layout
	 * 
	 * @param g
	 * 
	 * @param g
	 *            The layout group
	 * @param rightGroup
	 * @param leftGroup
	 * @param panel
	 */
	public abstract void init(GroupLayout layout, SequentialGroup g, ParallelGroup leftGroup, ParallelGroup rightGroup);

	public void addListener(SidebarActionListener listener) {
		this.listener.add(listener);
	}

	public void removeListener(SidebarActionListener listener) {
		this.listener.remove(listener);
	}

	protected void fireActionPerformed(SidebarAction action) {
		fireActionPerformed(action, null);
	}

	protected void fireActionPerformed(SidebarAction action, Object data) {
		for (SidebarActionListener l : this.listener) {
			l.sidebarActionPerformed(action, data);
		}
	}
	
	protected void fireDataChanged(AbstractNamedSimulationData data) {
		fireActionPerformed(SidebarAction.FIRE_SIMULATION_OBJECT_CHANGED, data);
	}

	/**
	 * Adds a component to the list of the components to show / hide
	 * 
	 * @param c
	 *            The component
	 */
	protected void addComponent(JComponent c) {
		components.add(c);
	}

	/**
	 * Hides all components of this field
	 */
	protected void hideComponents() {
		for (JComponent c : this.components) {
			c.setVisible(false);
		}
	}

	/**
	 * Shows all components of this field
	 */
	protected void showComponents() {
		for (JComponent c : this.components) {
			c.setVisible(true);
		}
	}

	/**
	 * Checks if this field can edit <code>data</code>
	 * 
	 * @return true if yes
	 */
	protected abstract boolean canHandleData(AbstractNamedSimulationData data);

	/**
	 * Loads the data from model to the view
	 */
	protected abstract void loadData();

	/**
	 * Unselect the current selection
	 */
	public abstract void noneSelected();

	/**
	 * A single element is selected
	 * 
	 * @param data
	 *            the selection data
	 */
	public abstract boolean dataSelected(AbstractNamedSimulationData data);

	/**
	 * Multiple elements are selected
	 * 
	 * @param data
	 *            the selection elements
	 */
	public abstract boolean dataSelected(AbstractNamedSimulationData[] data);

}
