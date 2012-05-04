package ch.zhaw.simulation.undo.action;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import ch.zhaw.simulation.model.AbstractSimulationModel;
import ch.zhaw.simulation.model.NamedFormulaData;

public class FormulaChangeUndoAction extends AbstractUndoableEdit {
	private NamedFormulaData data;
	private String oldFormula;
	private String newFormula;
	private AbstractSimulationModel<?> model;

	public FormulaChangeUndoAction(NamedFormulaData data, String oldFormula, String newFormula, AbstractSimulationModel<?> model) {
		this.data = data;
		this.oldFormula = oldFormula;
		this.newFormula = newFormula;
		this.model = model;
	}

	private void setFormula(String formula) {
		this.data.setFormula(formula);
		model.fireObjectChanged(this.data);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		setFormula(this.newFormula);
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		setFormula(this.oldFormula);
	}

	@Override
	public boolean addEdit(UndoableEdit e) {
		if (e instanceof FormulaChangeUndoAction) {
			FormulaChangeUndoAction n = (FormulaChangeUndoAction) e;

			if (this.data == n.data && this.newFormula.equals(n.oldFormula)) {
				this.newFormula = n.newFormula;

				return true;
			}

		}
		return false;
	}

	@Override
	public String getRedoPresentationName() {
		return "Ändern Formel von «" + data.getName() + "» auf «" + newFormula + "»";
	}

	@Override
	public String getUndoPresentationName() {
		return "Ändern Formel von «" + data.getName() + "» auf «" + oldFormula + "»";
	}
}
