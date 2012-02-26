package ch.zhaw.simulation.undo.action.flow;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationFlowModel;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class AddNamedSimulationUndoAction extends AbstractUndoableEdit {
	private NamedSimulationObject so;
	private SimulationFlowModel model;

	public AddNamedSimulationUndoAction(NamedSimulationObject so, SimulationFlowModel model) {
		this.so = so;
		this.model = model;
		add();
	}

	private void add() {
		model.addData(so);
	}

	@Override
	public void die() {
		super.die();

		so = null;
		model = null;
	}

	private String getName() {
		if (so.getName() != null) {
			return so.getName();
		}
		return "Objekt"; // Sollte nicht auftreten
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		add();
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		model.removeData(so);
	}

	@Override
	public String getRedoPresentationName() {
		return "Widerherstellen " + getName();
	}

	@Override
	public String getUndoPresentationName() {
		return "Lösche " + getName();
	}
}
