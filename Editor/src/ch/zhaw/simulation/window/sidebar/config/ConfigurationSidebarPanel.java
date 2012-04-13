package ch.zhaw.simulation.window.sidebar.config;

import java.util.Collections;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import org.jdesktop.swingx.JXTaskPane;

import ch.zhaw.simulation.frame.sidebar.SidebarPosition;
import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.AbstractSimulationData;
import ch.zhaw.simulation.model.element.TextData;
import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionListener;
import ch.zhaw.simulation.model.selection.SelectionModel;

public abstract class ConfigurationSidebarPanel<M extends AbstractSimulationModel<?>> extends JXTaskPane implements SelectionListener, SidebarPosition {
	private static final long serialVersionUID = 1L;

	/**
	 * The selection model to get the information about the current selection
	 */
	private SelectionModel selectionModel;

	/**
	 * The configuration files may shown
	 */
	private Vector<ConfigurationField> fields = new Vector<ConfigurationField>();

	/**
	 * The Layout of the sidebar
	 */
	private GroupLayout layout;

	/**
	 * The model to fire changes
	 */
	private M model;

	/**
	 * The listener to handle the events
	 */
	private SidebarActionListener listener = new SidebarActionListener() {

		@Override
		public void sidebarActionPerformed(SidebarAction action, Object data) {
			switch (action) {
			case SHOW_FORMULA_EDITOR:
				showFormulaEditor((AbstractNamedSimulationData) data);
				break;
				
			case FIRE_SIMULATION_OBJECT_CHANGED:
				model.fireObjectChanged((AbstractSimulationData) data);
				break;

			default:
				throw new RuntimeException("SidebarAction not handled: " + action);
			}
		}

	};

	public ConfigurationSidebarPanel(M model, SelectionModel selectionModel) {
		this.selectionModel = selectionModel;
		this.model = model;

		if (selectionModel == null) {
			throw new NullPointerException("selectionModel == null");
		}
		
		if (model == null) {
			throw new NullPointerException("model == null");
		}

		setTitle("Eigenschaften");
		setSpecial(true);

		setVisible(false);

		instanceConfigurationFields();

		this.layout = new GroupLayout(getContentPane());
		this.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		initConfigurationFiels();
	}
	
	public M getModel() {
		return model;
	}

	private void initConfigurationFiels() {
		Collections.sort(this.fields);

		ParallelGroup leftGroup = layout.createParallelGroup();
		ParallelGroup rightGroup = layout.createParallelGroup();
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(leftGroup).addGroup(rightGroup));

		SequentialGroup g = layout.createSequentialGroup();
		layout.setVerticalGroup(g);

		for (ConfigurationField f : this.fields) {
			f.init(this.layout, g, leftGroup, rightGroup);
		}
	}

	/**
	 * Initialize all configuration fiels
	 * 
	 * may override this method, but call
	 * <code>super.istanceConfigurationFields();</code>
	 */
	protected void instanceConfigurationFields() {
		addConfigurationField(new NameConfigurationField());
		addConfigurationField(new FormulaConfigurationField());
	}

	protected void addConfigurationField(ConfigurationField field) {
		field.addListener(listener);
		this.fields.add(field);
	}

	protected void removeConfigurationField(ConfigurationField field) {
		field.removeListener(listener);
		this.fields.remove(field);
	}

	public ConfigurationField[] getFields() {
		return fields.toArray(new ConfigurationField[] {});
	}

	/**
	 * Shows the formula editor to
	 * 
	 * @param data
	 *            The data to edit
	 */
	public abstract void showFormulaEditor(AbstractNamedSimulationData data);

	@Override
	public void selectionChanged() {
		SelectableElement<?>[] selected = selectionModel.getSelected();

		Vector<AbstractNamedSimulationData> selectedData = new Vector<AbstractNamedSimulationData>();

		for (SelectableElement<?> s : selected) {
			Object data = s.getData();
			if (data instanceof AbstractNamedSimulationData && !(data instanceof TextData)) {
				selectedData.add((AbstractNamedSimulationData) data);
			}
		}

		if (selectedData.size() == 0) {
			noneSelected();
		} else if (selectedData.size() == 1) {
			dataSelected(selectedData.get(0));
		} else {
			dataSelected(selectedData.toArray(new AbstractNamedSimulationData[] {}));
		}
	}

	private void dataSelected(AbstractNamedSimulationData[] data) {
		boolean active = false;
		for (ConfigurationField f : this.fields) {
			if (f.supportsMultibleEditing()) {
				active |= f.dataSelected(data);
			} else {
				f.noneSelected();
			}
		}

		// at least one field is visible
		setVisible(active);
	}

	private void dataSelected(AbstractNamedSimulationData data) {
		boolean active = false;

		for (ConfigurationField f : this.fields) {
			active |= f.dataSelected(data);
		}

		// at least one field is visible
		setVisible(active);
	}

	private void noneSelected() {
		for (ConfigurationField f : this.fields) {
			f.noneSelected();
		}

		// hide sidebar
		setVisible(false);
	}

	@Override
	public void selectionMoved(int dX, int dY) {
	}

	@Override
	public int getSidebarPosition() {
		return 0;
	}

	public void dispose() {
		for (ConfigurationField f : this.fields) {
			f.removeListener(listener);
		}

		this.fields.clear();
	}
}