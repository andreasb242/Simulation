package ch.zhaw.simulation.editor.layouting;

import ch.zhaw.simulation.model.selection.SelectableElement;
import ch.zhaw.simulation.model.selection.SelectionModel;
import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.action.LayoutUndoAction;

/**
 * This class handles layouting actions, e.g. align all selected elements to,
 * left right etc.
 * 
 * @author Andreas Butti
 */
public class Layouting {

	private SelectionModel selectionModel;
	private UndoHandler undo;

	public Layouting(SelectionModel selectionModel, UndoHandler undo) {
		this.selectionModel = selectionModel;
		this.undo = undo;
	}

	public void layoutTop() {
		SelectableElement[] elements = selectionModel.getSelected();
		if (elements.length < 2) {
			return;
		}

		LayoutUndoAction a = new LayoutUndoAction("Oben");

		int y = elements[0].getY();

		for (SelectableElement e : elements) {
			if (y > e.getY()) {
				y = e.getY();
			}
		}

		for (SelectableElement e : elements) {
			a.newPosition(e, 0, y - e.getY());
		}

		undo.addEdit(a);
	}

	public void layoutRight() {
		SelectableElement[] elements = selectionModel.getSelected();
		if (elements.length < 2) {
			return;
		}

		LayoutUndoAction a = new LayoutUndoAction("Rechts");

		int x = 0;

		for (SelectableElement e : elements) {
			if (x < e.getX()) {
				x = e.getX() + e.getWidth();
			}
		}

		for (SelectableElement e : elements) {
			a.newPosition(e, x - e.getX() - e.getWidth(), 0);
		}

		undo.addEdit(a);
	}

	public void layoutLeft() {
		SelectableElement[] elements = selectionModel.getSelected();
		if (elements.length < 2) {
			return;
		}

		LayoutUndoAction a = new LayoutUndoAction("Links");

		int x = elements[0].getX();

		for (SelectableElement e : elements) {
			if (x > e.getX()) {
				x = e.getX();
			}
		}

		for (SelectableElement e : elements) {
			a.newPosition(e, x - e.getX(), 0);
		}

		undo.addEdit(a);
	}

	public void layoutBottom() {
		SelectableElement[] elements = selectionModel.getSelected();
		if (elements.length < 2) {
			return;
		}

		LayoutUndoAction a = new LayoutUndoAction("Unten");

		int y = 0;

		for (SelectableElement e : elements) {
			if (y < e.getY()) {
				y = e.getY() + e.getHeight();
			}
		}

		for (SelectableElement e : elements) {
			a.newPosition(e, 0, y - e.getY() - e.getHeight());
		}

		undo.addEdit(a);
	}

	public void layoutCenterHorizontal() {
		SelectableElement[] elements = selectionModel.getSelected();
		if (elements.length < 2) {
			return;
		}

		LayoutUndoAction a = new LayoutUndoAction("Horizontal zentriert");

		int x = 0;

		for (SelectableElement e : elements) {
			x += e.getX() + e.getWidth() / 2;
		}

		x /= elements.length;

		for (SelectableElement e : elements) {
			a.newPosition(e, x - e.getX() - e.getWidth() / 2, 0);
		}

		undo.addEdit(a);
	}

	public void layoutCenterVertical() {
		SelectableElement[] elements = selectionModel.getSelected();
		if (elements.length < 2) {
			return;
		}

		LayoutUndoAction a = new LayoutUndoAction("Vertikal zentriert");

		int y = 0;

		for (SelectableElement e : elements) {
			y += e.getY() + e.getHeight() / 2;
		}

		y /= elements.length;

		for (SelectableElement e : elements) {
			a.newPosition(e, 0, y - e.getY() - e.getHeight() / 2);
		}

		undo.addEdit(a);
	}
}
