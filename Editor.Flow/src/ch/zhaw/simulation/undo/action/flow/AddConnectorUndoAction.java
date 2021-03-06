package ch.zhaw.simulation.undo.action.flow;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.model.flow.connection.AbstractConnectorData;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class AddConnectorUndoAction extends AbstractUndoableEdit {
	private AbstractConnectorData<?> c;
	private SimulationFlowModel model;

	public AddConnectorUndoAction(AbstractConnectorData<?> c, SimulationFlowModel model) {
		this.c = c;
		this.model = model;
		add();
	}

	private void add() {
		model.addConnector(c);
	}

	@Override
	public void die() {
		super.die();

		if (!hasBeenDone) { // Rückgängig gemacht
			c.dispose();
		}
		c = null;
		model = null;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		add();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		model.removeConnector(c);
	}

	@Override
	public String getRedoPresentationName() {
		return "Verbinder widerherstellen";
	}

	@Override
	public String getUndoPresentationName() {
		return "Verbinder löschen";
	}
}
