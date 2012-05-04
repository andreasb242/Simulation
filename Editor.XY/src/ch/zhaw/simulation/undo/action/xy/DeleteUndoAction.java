package ch.zhaw.simulation.undo.action.xy;

import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class DeleteUndoAction extends AbstractUndoableEdit {
	private Vector<AbstractNamedSimulationData> removedObjects;
	private XYEditorControl control;

	public DeleteUndoAction(Vector<AbstractNamedSimulationData> removedObjects, XYEditorControl control) {
		this.removedObjects = removedObjects;
		this.control = control;

		delete();
	}

	@Override
	public void die() {
		super.die();

		removedObjects.clear();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		SimulationXYModel model = control.getModel();

		for (AbstractNamedSimulationData o : removedObjects) {
			model.addData(o);
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		delete();
	}

	private void delete() {
		SimulationXYModel model = control.getModel();

		for (AbstractNamedSimulationData o : removedObjects) {
			model.removeData(o);
		}
	}

	private String getNames() {
		StringBuilder b = new StringBuilder();

		if (removedObjects.size() == 0) {
			return "Verbinder";
		}

		for (AbstractNamedSimulationData o : removedObjects) {
			b.append(", ");
			b.append(o.getName());
		}

		return b.substring(2);
	}

	@Override
	public String getRedoPresentationName() {
		return "Löschen «" + getNames() + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Widerherstellen «" + getNames() + "»";
	}

}
