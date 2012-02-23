package ch.zhaw.simulation.undo.action;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.flow.NamedSimulationObject;
import ch.zhaw.simulation.model.flow.SimulationDocument;


public class AddNamedSimulationUndoAction extends AbstractUndoableEdit {
	private static final long serialVersionUID = 1L;
	private NamedSimulationObject so;
	private SimulationDocument model;

	public AddNamedSimulationUndoAction(NamedSimulationObject so,
			SimulationDocument model) {
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
