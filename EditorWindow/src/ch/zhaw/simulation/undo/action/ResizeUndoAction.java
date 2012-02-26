package ch.zhaw.simulation.undo.action;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import ch.zhaw.simulation.editor.view.CommentView;

public class ResizeUndoAction extends AbstractUndoableEdit {
	private CommentView element;
	private int dX;
	private int dY;

	public ResizeUndoAction(CommentView element, int dX, int dY) {
		this.element = element;
		this.dX = dX;
		this.dY = dY;
		element.changeSize(dX, dY);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		element.changeSize(dX, dY);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		element.changeSize(-dX, -dY);
	}

	@Override
	public boolean addEdit(UndoableEdit e) {
		if (e instanceof ResizeUndoAction) {
			ResizeUndoAction a = (ResizeUndoAction) e;
			if (element.equals(((ResizeUndoAction) e).element)) {
				this.dX += a.dX;
				this.dY += a.dY;

				return true;
			}

		}
		return false;
	}

	@Override
	public String getRedoPresentationName() {
		return "Grösse ändern widerherstellen";
	}

	@Override
	public String getUndoPresentationName() {
		return "Grösse ändern rückgängig";
	}
}
