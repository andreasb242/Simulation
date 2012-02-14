package ch.zhaw.simulation.model.selection;

import java.util.Vector;

public class SelectionModel {
	private Vector<SelectableElement> selected = new Vector<SelectableElement>();
	private Vector<SelectableElement> tmpSelection = new Vector<SelectableElement>();

	private Vector<SelectionListener> listener = new Vector<SelectionListener>();

	/**
	 * Für Globale etc. Markieren was von was verwendet wird / abhängig ist
	 */
	private Vector<SelectableElement> dependentElement = new Vector<SelectableElement>();

	public SelectionModel() {
	}

	public void setSelected(SelectableElement e) {
		selected.clear();
		selected.add(e);

		tmpSelection.clear();

		fireSelectionChanged();
	}

	/**
	 * Prüft ob ein Objekt abhängig ist / verwendet wird
	 * 
	 * @param e
	 * @return true wenn ja
	 */
	public boolean isDependentElement(SelectableElement e) {
		return dependentElement.contains(e);
	}

	public void setDependentElement(Vector<SelectableElement> elements) {
		dependentElement.clear();
		dependentElement.addAll(elements);
	}

	/**
	 * Fügt eine Selektion hinzu ohne einen Event abzusenden, für Loops etc.
	 * 
	 * @param e
	 *            Das zu selektierende Element
	 */
	public void addSelectedInt(SelectableElement e) {
		if (selected.contains(e)) {
			return;
		}
		selected.add(e);
	}

	public void addSelected(SelectableElement e) {
		addSelectedInt(e);
		fireSelectionChanged();
	}

	public void setTmpSelection(Vector<SelectableElement> tmp) {
		tmpSelection.clear();
		for (SelectableElement e : tmp) {
			addTmpSelection(e);
		}
		fireSelectionChanged();
	}

	public void setTmpSelection(SelectableElement elem) {
		tmpSelection.clear();
		addTmpSelection(elem);
		fireSelectionChanged();
	}

	public void addTmpSelection(SelectableElement elem) {
		if (!selected.contains(elem)) {
			tmpSelection.add(elem);
		}
	}

	public void acceptTmpSelection() {
		for (SelectableElement e : tmpSelection) {
			addSelectedInt(e);
		}
		tmpSelection.clear();
		fireSelectionChanged();
	}

	public void removeSelected(SelectableElement e) {
		selected.remove(e);
		fireSelectionChanged();
	}

	public SelectableElement getSingleSelectedElement() {
		if (selected.size() == 1) {
			return selected.get(0);
		}
		return null;
	}

	public SelectableElement[] getSelected() {
		Vector<SelectableElement> all = new Vector<SelectableElement>();
		all.addAll(selected);
		all.addAll(tmpSelection);

		return all.toArray(new SelectableElement[] {});
	}

	public boolean isSelected(SelectableElement e) {
		return selected.contains(e) || tmpSelection.contains(e);
	}

	public void addSelectionListener(SelectionListener l) {
		listener.add(l);
	}

	public void removeSelectionListener(SelectionListener l) {
		listener.remove(l);
	}

	public void fireSelectionChanged() {
		for (SelectionListener l : listener) {
			l.selectionChanged();
		}
	}

	private void fireSelectionMoved(int dX, int dY) {
		for (SelectionListener l : listener) {
			l.selectionMoved(dX, dY);
		}
	}

	/**
	 * Verschiebt alle selektierten Elemente um dX und dY
	 * 
	 * @param dX
	 * @param dY
	 */
	public void move(int dX, int dY) {
		int moveLeft = -dX;
		int moveTop = -dY;

		for (SelectableElement el : getSelected()) {
			moveLeft = Math.min(moveLeft, el.getX());
			moveTop = Math.min(moveTop, el.getY());
		}

		for (SelectableElement el : getSelected()) {
			el.moveElement(-moveLeft, -moveTop);
		}

		fireSelectionMoved(dX, dY);
	}

	public void clearSelection() {
		tmpSelection.clear();
		selected.clear();

		fireSelectionChanged();
	}

	public void clearTmpSelection() {
		tmpSelection.clear();
		fireSelectionChanged();
	}

	public boolean hasSelected() {
		return tmpSelection.size() != 0 || selected.size() != 0;
	}

	public void clearDependentElement() {
		dependentElement.clear();
	}
}
