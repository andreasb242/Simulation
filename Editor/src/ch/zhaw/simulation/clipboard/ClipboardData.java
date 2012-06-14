package ch.zhaw.simulation.clipboard;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;

public interface ClipboardData {
	/**
	 * Insert the clipboard contents to the model
	 * 
	 * @return true on success (or partwise success), false if nothing inserted,
	 *         because its the wrong data for this model
	 */
	public boolean addToModel(AbstractEditorControl<?> control);

	public int getEditorSourceId();

	public int getTransferableId();

}
