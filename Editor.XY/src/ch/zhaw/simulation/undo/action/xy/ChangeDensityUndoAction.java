package ch.zhaw.simulation.undo.action.xy;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.xy.DensityData;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class ChangeDensityUndoAction extends AbstractUndoableEdit {
	private DensityData data;
	private SimulationXYModel model;
	private String oldName;
	private String oldDescription;
	private String oldFormula;
	private String newName;
	private String newDescription;
	private String newFormula;

	public ChangeDensityUndoAction(DensityData data, String oldName, String oldDescription, String oldFormula, String newName, String newDescription,
			String newFormula, SimulationXYModel model) {

		this.data = data;

		this.oldName = oldName;
		this.oldDescription = oldDescription;
		this.oldFormula = oldFormula;
		this.newName = newName;
		this.newDescription = newDescription;
		this.newFormula = newFormula;

		this.model = model;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		this.data.setName(oldName);
		this.data.setDescription(oldDescription);
		this.data.setFormula(oldFormula);

		this.model.fireObjectChanged(this.data);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		this.data.setName(newName);
		this.data.setDescription(newDescription);
		this.data.setFormula(newFormula);

		this.model.fireObjectChanged(this.data);
	}

	@Override
	public String getRedoPresentationName() {
		return "Dichte ändern widerherstellen «" + data.getName() + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Dichte ändern rückgängig«" + data.getName() + "»";
	}

}
