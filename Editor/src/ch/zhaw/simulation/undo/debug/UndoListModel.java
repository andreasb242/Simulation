package ch.zhaw.simulation.undo.debug;

import javax.swing.AbstractListModel;

import ch.zhaw.simulation.undo.UndoHandler;
import ch.zhaw.simulation.undo.UndoListener;

public class UndoListModel extends AbstractListModel implements UndoListener {
	private static final long serialVersionUID = 1L;
	private UndoHandler undoHandler;

	public UndoListModel(UndoHandler undoHandler) {
		this.undoHandler = undoHandler;
		undoHandler.addUndoListener(this);
	}

	@Override
	public int getSize() {
		return undoHandler.getUndoCount();
	}

	@Override
	public Object getElementAt(int index) {
		return undoHandler.getUndoElement(getSize() - index - 1);
	}

	@Override
	public void undoRedoUpdated() {
		fireContentsChanged(0, 0, getSize());
	}
}
