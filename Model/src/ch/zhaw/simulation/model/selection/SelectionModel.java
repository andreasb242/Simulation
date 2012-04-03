package ch.zhaw.simulation.model.selection;

import java.util.Vector;

/**
 * The selection Model, not really part of the Document, its only the current
 * selected elements in a View
 * 
 * @author Andreas Butti
 */
public class SelectionModel {
	/**
	 * The selected elements
	 */
	private Vector<SelectableElement<?>> selected = new Vector<SelectableElement<?>>();

	/**
	 * While selecting with the mouse these are the current marked elements, but
	 * they are not yet selected
	 */
	private Vector<SelectableElement<?>> tmpSelection = new Vector<SelectableElement<?>>();

	/**
	 * The listeners...
	 */
	private Vector<SelectionListener> listener = new Vector<SelectionListener>();

	/**
	 * This is used for globals to mark the used, or where use them (the shadow
	 * if you select a SimulationObject wich uses a global)
	 */
	private Vector<SelectableElement<?>> dependentElement = new Vector<SelectableElement<?>>();

	public SelectionModel() {
	}

	/**
	 * Sets this a current selected single element
	 * 
	 * @param e
	 *            The selected element
	 */
	public void setSelected(SelectableElement<?>[] elements) {
		selected.clear();
		addSelectedInt(elements);

		tmpSelection.clear();

		fireSelectionChanged();
	}

	/**
	 * Sets this a current selected single element
	 * 
	 * @param e
	 *            The selected element
	 */
	public void setSelected(SelectableElement<?> e) {
		selected.clear();
		addSelectedInt(e);

		tmpSelection.clear();

		fireSelectionChanged();
	}

	/**
	 * Prüft ob ein Objekt abhängig ist / verwendet wird
	 * 
	 * @param e
	 * @return true wenn ja
	 */
	public boolean isDependentElement(SelectableElement<?> e) {
		return dependentElement.contains(e);
	}

	/**
	 * Sets wich are the dependent objecs of this e.g. global
	 */
	public void setDependentElement(Vector<SelectableElement<?>> elements) {
		dependentElement.clear();
		dependentElement.addAll(elements);
	}

	/**
	 * Clear the demependece list
	 */
	public void clearDependentElement() {
		dependentElement.clear();
	}

	/**
	 * Fügt eine Selektion hinzu ohne einen Event abzusenden, für Loops etc.
	 * 
	 * @param e
	 *            Die zu selektierende Element
	 */
	public void addSelectedInt(SelectableElement<?>[] elements) {
		for (SelectableElement<?> e : elements) {
			addSelectedInt(e);
		}
	}

	/**
	 * Fügt eine Selektion hinzu ohne einen Event abzusenden, für Loops etc.
	 * 
	 * @param e
	 *            Das zu selektierende Element
	 */
	public void addSelectedInt(SelectableElement<?> e) {
		if (selected.contains(e)) {
			return;
		}
		selected.add(e);
	}

	/**
	 * Adds a selected element
	 */
	public void addSelected(SelectableElement<?>[] elements) {
		addSelectedInt(elements);
		fireSelectionChanged();
	}

	/**
	 * Adds a selected element
	 */
	public void addSelected(SelectableElement<?> e) {
		addSelectedInt(e);
		fireSelectionChanged();
	}

	/**
	 * Sets the current marked elements, but they are not yet selected
	 */
	public void setTmpSelection(Vector<SelectableElement<?>> tmp) {
		tmpSelection.clear();
		for (SelectableElement<?> e : tmp) {
			addTmpSelection(e);
		}
		fireSelectionChanged();
	}

	/**
	 * Sets the current marked single element
	 */
	public void setTmpSelection(SelectableElement<?> elem) {
		tmpSelection.clear();
		addTmpSelection(elem);
		fireSelectionChanged();
	}

	/**
	 * Adds a new element to the marked elements
	 */
	public void addTmpSelection(SelectableElement<?> elem) {
		if (!selected.contains(elem)) {
			tmpSelection.add(elem);
		}
	}

	/**
	 * The current marked elements are now really selected
	 */
	public void acceptTmpSelection() {
		for (SelectableElement<?> e : tmpSelection) {
			addSelectedInt(e);
		}
		tmpSelection.clear();
		fireSelectionChanged();
	}

	/**
	 * Unselected an element
	 */
	public void removeSelected(SelectableElement<?> e) {
		selected.remove(e);
		fireSelectionChanged();
	}

	/**
	 * Unselected an element
	 */
	public void removeSelected(SelectableElement<?>[] elements) {
		for (SelectableElement<?> e : elements) {
			selected.remove(e);
		}
		fireSelectionChanged();
	}

	/**
	 * Return the single selected element
	 * 
	 * @return The Single element or null if multiple or nothing is selected
	 */
	public SelectableElement<?> getSingleSelectedElement() {
		if (selected.size() == 1) {
			return selected.get(0);
		}
		return null;
	}

	/**
	 * Returns all selected elements
	 * 
	 * @return A new allocated array
	 */
	public SelectableElement<?>[] getSelected() {
		Vector<SelectableElement<?>> all = new Vector<SelectableElement<?>>();
		all.addAll(selected);
		all.addAll(tmpSelection);

		return all.toArray(new SelectableElement[] {});
	}

	/**
	 * Is this element the selected?
	 * 
	 * @param e
	 *            The element
	 * @return true if selected
	 */
	public boolean isSelected(SelectableElement<?> e) {
		return selected.contains(e) || tmpSelection.contains(e);
	}

	/**
	 * Adds a selection listener
	 */
	public void addSelectionListener(SelectionListener l) {
		listener.add(l);
	}

	/**
	 * Removes a selection listener
	 */
	public void removeSelectionListener(SelectionListener l) {
		listener.remove(l);
	}

	/**
	 * Fires if the selection has changed
	 */
	public void fireSelectionChanged() {
		for (SelectionListener l : listener) {
			l.selectionChanged();
		}
	}

	/**
	 * Fire the selection has moved
	 * 
	 * @param dX
	 *            Delta X
	 * @param dY
	 *            Delta Y
	 */
	private void fireSelectionMoved(int dX, int dY) {
		for (SelectionListener l : listener) {
			l.selectionMoved(dX, dY);
		}
	}

	/**
	 * Moves all selected elements
	 * 
	 * @param dX
	 *            Delta X
	 * @param dY
	 *            Delta Y
	 */
	public void move(int dX, int dY) {
		int moveLeft = -dX;
		int moveTop = -dY;

		for (SelectableElement<?> el : getSelected()) {
			moveLeft = Math.min(moveLeft, el.getX());
			moveTop = Math.min(moveTop, el.getY());
		}

		for (SelectableElement<?> el : getSelected()) {
			el.moveElement(-moveLeft, -moveTop);
		}

		fireSelectionMoved(dX, dY);
	}

	/**
	 * Cleares the current selection and the marked objects
	 */
	public void clearSelection() {
		tmpSelection.clear();
		selected.clear();

		fireSelectionChanged();
	}

	/**
	 * Clears only the marked objects
	 */
	public void clearTmpSelection() {
		tmpSelection.clear();
		fireSelectionChanged();
	}

	/**
	 * If something is selected?
	 * 
	 * @return true if yes
	 */
	public boolean hasSelected() {
		return tmpSelection.size() != 0 || selected.size() != 0;
	}

}
