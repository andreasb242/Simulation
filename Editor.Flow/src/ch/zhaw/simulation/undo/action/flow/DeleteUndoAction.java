package ch.zhaw.simulation.undo.action.flow;

import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.Connector;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class DeleteUndoAction extends AbstractUndoableEdit {
	private Vector<NamedSimulationObject> removedObjects;
	private Vector<Connector<?>> removedConnectors;
	private FlowEditorControl control;
	private Vector<InfiniteData> removedInfinite;

	public DeleteUndoAction(Vector<NamedSimulationObject> removedObjects, Vector<Connector<?>> removedConnectors, Vector<InfiniteData> removedInfinite,
			FlowEditorControl control) {
		this.removedObjects = removedObjects;
		this.removedConnectors = removedConnectors;
		this.removedInfinite = removedInfinite;
		this.control = control;

		delete();
	}

	@Override
	public void die() {
		super.die();

		removedObjects.clear();

		// Gemacht, also nicht rückgängig gemacht, somit kann
		// es nun wirklich gelöscht werden
		if (hasBeenDone) {
			for (Connector<?> c : removedConnectors) {
				c.dispose();
			}
		}
		removedConnectors.clear();
		removedInfinite.clear();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		SimulationFlowModel model = control.getModel();

		for (NamedSimulationObject o : removedObjects) {
			model.addData(o);
		}

		for (InfiniteData i : removedInfinite) {
			model.addData(i);
		}

		for (Connector<?> c : removedConnectors) {
			model.addConnector(c);
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		delete();
	}

	private void delete() {
		SimulationFlowModel model = control.getModel();

		for (Connector<?> c : removedConnectors) {
			model.removeConnector(c);
		}

		for (InfiniteData i : removedInfinite) {
			model.removeData(i);
		}

		for (NamedSimulationObject o : removedObjects) {
			model.removeData(o);
		}
	}

	private String getNames() {
		StringBuilder b = new StringBuilder();

		if (removedObjects.size() == 0) {
			return "Verbinder";
		}

		for (NamedSimulationObject o : removedObjects) {
			b.append(", ");
			b.append(o.getName());
		}

		return b.substring(2);
	}

	@Override
	public String getRedoPresentationName() {
		return "Löschen: " + getNames();
	}

	@Override
	public String getUndoPresentationName() {
		return "Widerherstellen: " + getNames();
	}

}
