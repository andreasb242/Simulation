package ch.zhaw.simulation.undo.action;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.model.selection.SelectableElement;

public class MoveUndoAction extends PositionUndoAction {
	private int dX;
	private int dY;

	public MoveUndoAction(SelectableElement<?>[] elements, int dX, int dY, AbstractEditorView<?> view) {
		super(elements, view);
		this.dX = dX;
		this.dY = dY;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		move(1);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		move(-1);
	}

	private void move(int factor) {
		int dX = this.dX * factor;
		int dY = this.dY * factor;

		for (SelectableElement<?> e : this) {
			e.moveElement(dX, dY);
		}
	}

	@Override
	public boolean addEdit(UndoableEdit e) {
		if (e instanceof MoveUndoAction) {
			MoveUndoAction a = (MoveUndoAction) e;
			if (equalsElements(a)) {
				this.dX += a.dX;
				this.dY += a.dY;

				return true;
			}

		}
		return false;
	}

	@Override
	public String getRedoPresentationName() {
		return "Verschieben widerherstellen";
	}

	@Override
	public String getUndoPresentationName() {
		return "Verschieben rückgängig";
	}
}
