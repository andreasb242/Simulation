package ch.zhaw.simulation.undo.action;

import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import ch.zhaw.simulation.model.flow.selection.SelectableElement;


public class MoveUndoAction extends AbstractUndoableEdit {

	private SelectableElement[] elements;
	private int dX;
	private int dY;

	public MoveUndoAction(SelectableElement[] elements, int dX, int dY) {
		this.elements = elements;
		this.dX = dX;
		this.dY = dY;
	}
	
	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		move(1);
	}
	
	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		move(-1);
	}
	
	private void move(int factor) {
		int dX = this.dX * factor;
		int dY = this.dY * factor;
		
		for(SelectableElement e : elements) {
			e.moveElement(dX, dY);
		}
	}
	
	@Override
	public boolean addEdit(UndoableEdit e) {
		if(e instanceof MoveUndoAction) {
			MoveUndoAction a = (MoveUndoAction) e;
			if(equalsElements(a.elements)) {
				this.dX += a.dX;
				this.dY += a.dY;
				
				return true;
			}
			
		}
		return false;
	}
	
	private boolean equalsElements(SelectableElement[] elem) {
		Vector<SelectableElement> list = new Vector<SelectableElement>();
		for(SelectableElement e : elem) {
			list.add(e);
		}
		
		for(SelectableElement e : elements) {
			if(!list.remove(e)) {
				return false;
			}
		}
		
		return list.size() == 0;
	}
	
	@Override
	public String getRedoPresentationName() {
		return "Verschieben widerherstellen";
	}

	@Override
	public String getUndoPresentationName() {
		return "Verschieben rückgängig";
	}
}
