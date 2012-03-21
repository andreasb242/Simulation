package ch.zhaw.simulation.window.xy.sidebar;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.model.xy.SubModelList;
import ch.zhaw.simulation.model.xy.SubModelListener;

public class SubmodelComboboxModel extends AbstractListModel implements ComboBoxModel, SubModelListener {
	private static final long serialVersionUID = 1L;

	private Object selectedObject;
	private SubModelList model;

	public SubmodelComboboxModel(SubModelList model) {
		this.model = model;
		model.addListener(this);
	}

	public void setSelectedItem(Object anObject) {
		if ((selectedObject != null && !selectedObject.equals(anObject)) || selectedObject == null && anObject != null) {
			selectedObject = anObject;
			fireContentsChanged(this, -1, -1);
		}
	}

	// implements javax.swing.ComboBoxModel
	public Object getSelectedItem() {
		return selectedObject;
	}

	@Override
	public int getSize() {
		return model.getSize();
	}

	@Override
	public Object getElementAt(int index) {
		return this.model.getModel(index);
	}

	public void dispose() {
		model.removeListener(this);
	}

	@Override
	public void submodelRemoved(SubModel model) {
		// Always everything changed, because the colors change
		fireContentsChanged(this, 0, getSize());
	}

	@Override
	public void submodelAdded(SubModel model) {
		// Always everything changed, because the colors change
		fireContentsChanged(this, 0, getSize());
	}

	@Override
	public void submodelChanged(SubModel model) {
		// Always everything changed, because the colors change
		fireContentsChanged(this, 0, getSize());
	}
}
