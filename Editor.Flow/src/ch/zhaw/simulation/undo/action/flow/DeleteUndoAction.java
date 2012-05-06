package ch.zhaw.simulation.undo.action.flow;

import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.model.flow.element.InfiniteData;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class DeleteUndoAction extends AbstractUndoableEdit {
	private Vector<AbstractNamedSimulationData> removedObjects;
	private Vector<AbstractConnectorData<?>> removedConnectors;
	private FlowEditorControl control;
	private Vector<InfiniteData> removedInfinite;

	public DeleteUndoAction(Vector<AbstractNamedSimulationData> removedObjects, Vector<AbstractConnectorData<?>> removedConnectors,
			Vector<InfiniteData> removedInfinite, FlowEditorControl control) {
		this.removedObjects = removedObjects;
		this.removedConnectors = removedConnectors;
		this.removedInfinite = removedInfinite;
		this.control = control;

		if(this.removedObjects.size() == 0) {
			throw new RuntimeException("flow.DeleteUndoAction.removedObjects.size() == 0");
		}
		
		delete();
	}

	@Override
	public void die() {
		super.die();

		removedObjects.clear();

		// Gemacht, also nicht rückgängig gemacht, somit kann
		// es nun wirklich gelöscht werden
		if (hasBeenDone) {
			for (AbstractConnectorData<?> c : removedConnectors) {
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

		for (AbstractNamedSimulationData o : removedObjects) {
			model.addData(o);
		}

		for (InfiniteData i : removedInfinite) {
			model.addData(i);
		}

		for (AbstractConnectorData<?> c : removedConnectors) {
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

		for (AbstractConnectorData<?> c : removedConnectors) {
			model.removeConnector(c);
		}

		for (InfiniteData i : removedInfinite) {
			model.removeData(i);
		}

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
		return "Löschen: «" + getNames() + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Widerherstellen: «" + getNames() + "»";
	}

}
