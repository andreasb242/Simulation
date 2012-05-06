package ch.zhaw.simulation.undo.action;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.TextData;

public class TextEditUndoAction extends AbstractUndoableEdit {
	private TextData textData;
	private String oldTxt;
	private String newText;
	private AbstractSimulationModel<?> model;

	public TextEditUndoAction(TextData textData, String oldTxt, String newText, AbstractSimulationModel<?> model) {
		this.textData = textData;
		this.oldTxt = oldTxt;
		this.newText = newText;
		this.model = model;
	}

	@Override
	public void die() {
		this.textData = null;
		this.oldTxt = null;
		this.newText = null;
		this.model = null;

		super.die();
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		this.textData.setText(newText);
		model.fireObjectChanged(this.textData);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		this.textData.setText(oldTxt);
		model.fireObjectChanged(this.textData);
	}

	@Override
	public String getRedoPresentationName() {
		return "Text ändern widerholen";
	}

	@Override
	public String getUndoPresentationName() {
		return "Text ändern rückgängig";
	}
}
