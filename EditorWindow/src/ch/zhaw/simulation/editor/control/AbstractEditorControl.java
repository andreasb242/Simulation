package ch.zhaw.simulation.editor.control;

import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;
import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;

/**
 * The controler of a model editor
 * 
 * @author Andreas Butti
 */
public abstract class AbstractEditorControl {
	/**
	 * The selection model, contains the current selected gui elements
	 */
	protected SelectionModel selectionModel = new SelectionModel();

	/**
	 * The system integration object
	 */
	private Sysintegration integration;

	/**
	 * CTor
	 */
	public AbstractEditorControl() {
		integration = SysintegrationFactory.createSysintegration();
	}

	/**
	 * Deletes the current selected elements
	 */
	public void deleteSelected() {
		SelectableElement[] selected = selectionModel.getSelected();
		selectionModel.clearSelection();

		delete(selected);
	}

	/**
	 * Deletes the elements from the model and may also other depending objects
	 */
	protected abstract void delete(SelectableElement[] elements);

	/**
	 * @return The selecion model
	 */
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @return The system integration object
	 */
	public Sysintegration getSysintegration() {
		return integration;
	}

}
