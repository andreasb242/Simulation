package ch.zhaw.simulation.undo.action.xy;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.model.xy.SimulationXYModel;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.model.xy.SubModelList;
import ch.zhaw.simulation.undo.action.AbstractUndoableEdit;

public class SubModelCreateUndoAction extends AbstractUndoableEdit {
	private SubModel sub;
	private XYEditorControl control;

	public SubModelCreateUndoAction(SubModel sub, XYEditorControl control) {
		this.sub = sub;
		this.control = control;
	}

	@Override
	public void die() {
		super.die();

		this.sub = null;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		SimulationXYModel model = control.getModel();
		SubModelList m = model.getSubmodels();

		m.removeModel(this.sub);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		SimulationXYModel model = control.getModel();
		SubModelList m = model.getSubmodels();

		m.addModel(this.sub);
	}

	@Override
	public String getRedoPresentationName() {
		return "Submodel löschen: «" + sub.getName() + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Submodel widerherstellen: «" + sub.getName() + "»";
	}

}
