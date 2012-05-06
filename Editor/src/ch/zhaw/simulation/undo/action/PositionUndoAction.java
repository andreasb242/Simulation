package ch.zhaw.simulation.undo.action;

import java.util.Iterator;
import java.util.Vector;

import ch.zhaw.simulation.editor.view.AbstractEditorView;
import ch.zhaw.simulation.model.selection.SelectableElement;

public abstract class PositionUndoAction extends AbstractUndoableEdit implements Iterable<SelectableElement<?>> {
	private Vector<Object> data = new Vector<Object>();
	private AbstractEditorView<?> view;

	public PositionUndoAction(SelectableElement<?>[] elements, AbstractEditorView<?> view) {
		this.view = view;
		
		for (SelectableElement<?> e : elements) {
			data.add(e.getData());
		}
	}

	protected boolean equalsElements(PositionUndoAction a) {
		Vector<Object> list = new Vector<Object>();
		for (Object o : a.data) {
			list.add(o);
		}

		for (Object o : this.data) {
			if (!list.remove(o)) {
				return false;
			}
		}

		return list.size() == 0;
	}

	@Override
	public Iterator<SelectableElement<?>> iterator() {
		return new Iterator<SelectableElement<?>>() {
			private Iterator<Object> it;

			{
				it = data.iterator();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove not allowed");
			}

			@Override
			public SelectableElement<?> next() {
				return view.findGuiComponentForObj(it.next());
			}

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
		};
	}
}
