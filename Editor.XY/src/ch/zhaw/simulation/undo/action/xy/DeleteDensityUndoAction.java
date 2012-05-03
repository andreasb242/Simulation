package ch.zhaw.simulation.undo.action.xy;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class DeleteDensityUndoAction extends AbstractUndoableEdit {
	private DensityData data;
	private SimulationXYModel model;

	public DeleteDensityUndoAction(DensityData data, SimulationXYModel model) {
		this.data = data;
		this.model = model;
	}

	@Override
	public void die() {
		super.die();
		this.data = null;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		this.model.addDensity(this.data);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		this.model.removeDensity(this.data);
	}

	@Override
	public String getRedoPresentationName() {
		return "Löschen Dichte «" + data.getName() + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Widerherstellen Dichte «" + data.getName() + "»";
	}

}
