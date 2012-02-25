package ch.zhaw.simulation.editor.control;

import ch.zhaw.simulation.gui.control.GuiConfig;
import ch.zhaw.simulation.model.flow.selection.SelectableElement;
import ch.zhaw.simulation.model.flow.selection.SelectionModel;

/**
 * The controler of a model editor
 * 
 * @author Andreas Butti
 */
public abstract class AbstractEditorControl {
	/**
	 * The color and size configuration
	 */
	private GuiConfig config = new GuiConfig();

	/**
	 * The selection model, contains the current selected gui elements
	 */
	protected SelectionModel selectionModel = new SelectionModel();

	/**
	 * CTor
	 */
	public AbstractEditorControl() {
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
	 * @return The color and size configuration
	 */
	public GuiConfig getConfig() {
		return config;
	}
}
