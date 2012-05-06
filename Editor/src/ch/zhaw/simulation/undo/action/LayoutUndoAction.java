package ch.zhaw.simulation.undo.action;

import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.model.selection.SelectableElement;

public class LayoutUndoAction extends AbstractUndoableEdit {
	private Vector<ElemPoint> moves = new Vector<ElemPoint>();
	private String name;
	private AbstractEditorView<?> view;

	public LayoutUndoAction(String name, AbstractEditorView<?> view) {
		this.name = name;
		this.view = view;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		move(true);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		move(false);
	}

	private void move(boolean back) {
		for (ElemPoint p : moves) {
			p.move(back);
		}
	}

	@Override
	public String getRedoPresentationName() {
		return "Ausrichten «" + name + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Ausrichten rückgängig";
	}

	public void addPos() {
	}

	public void newPosition(SelectableElement<?> e, int dX, int dY) {
		e.moveElement(dX, dY);
		moves.add(new ElemPoint(e.getData(), dX, dY));
	}

	class ElemPoint {
		protected Object data;
		private int dX;
		private int dY;

		protected ElemPoint() {
		}

		public ElemPoint(Object data, int dX, int dY) {
			this.data = data;
			this.dX = dX;
			this.dY = dY;
		}

		public void move(boolean back) {
			SelectableElement<?> e = view.findGuiComponentForObj(this.data);

			if (back) {
				e.moveElement(dX, dY);
			} else {
				e.moveElement(-dX, -dY);
			}
		}
	}
}
