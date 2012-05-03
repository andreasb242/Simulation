package ch.zhaw.simulation.undo.action;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;

public class NameChangeUndoAction extends AbstractUndoableEdit {
	private AbstractNamedSimulationData data;
	private String oldName;
	private String newName;
	private AbstractSimulationModel<?> model;

	public NameChangeUndoAction(AbstractNamedSimulationData data, String oldName, String newName, AbstractSimulationModel<?> model) {
		this.data = data;
		this.oldName = oldName;
		this.newName = newName;
		this.model = model;

		setName(newName);
	}

	private void setName(String name) {
		this.data.setName(name);
		model.fireObjectChanged(this.data);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		setName(this.newName);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		setName(this.oldName);
	}

	@Override
	public boolean addEdit(UndoableEdit e) {
		if (e instanceof NameChangeUndoAction) {
			NameChangeUndoAction n = (NameChangeUndoAction) e;

			if (this.data == n.data && this.newName.equals(n.oldName)) {
				this.newName = n.newName;

				return true;
			}

		}
		return false;
	}

	@Override
	public String getRedoPresentationName() {
		return "Name von «" + oldName + "» auf «" + newName + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Name von «" + newName + "» auf «" + oldName + "»";
	}
}
