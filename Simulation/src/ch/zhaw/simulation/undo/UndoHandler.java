package ch.zhaw.simulation.undo;

import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;


import butti.javalibs.gui.messagebox.Messagebox;

import butti.javalibs.errorhandler.Errorhandler;

public class UndoHandler extends UndoManager {
	private static final long serialVersionUID = 1L;

	private Vector<UndoListener> undoListener = new Vector<UndoListener>();

	public void undo() {
		try {
			super.undo();
		} catch (CannotRedoException e) {
			Messagebox.showError(null, "Rückgängig", "Rückgängig war doch nicht möglich... Sorry...");
		} catch (Exception e) {
			Errorhandler.showError(e);
		}
		fireUpdateUndoRedo();
	}

	public void redo() {
		try {
			super.redo();
		} catch (CannotRedoException e) {
			Messagebox.showError(null, "Widerherstellen", "Widerherstellen war doch nicht möglich... Sorry...");
		} catch (Exception e) {
			Errorhandler.showError(e);
		}
		fireUpdateUndoRedo();
	}

	public void addUndoListener(UndoListener l) {
		undoListener.add(l);
	}

	public void removeUndoListener(UndoListener l) {
		undoListener.remove(l);
	}

	private void fireUpdateUndoRedo() {
		for (UndoListener l : undoListener) {
			l.undoRedoUpdated();
		}
	}

	@Override
	public synchronized boolean addEdit(UndoableEdit anEdit) {
		boolean b = super.addEdit(anEdit);
		fireUpdateUndoRedo();
		return b;
	}

	@Override
	public synchronized void discardAllEdits() {
		super.discardAllEdits();
		fireUpdateUndoRedo();
	}
}
