package ch.zhaw.simulation.undo.action;

import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.zhaw.simulation.model.selection.SelectableElement;


public class LayoutUndoAction extends AbstractUndoableEdit {

	private Vector<ElemPoint> moves = new Vector<ElemPoint>();
	private String name;

	public LayoutUndoAction(String name) {
		this.name = name;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		move(true);
	}
	
	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		move(false);
	}
	
	private void move(boolean back) {
		for(ElemPoint p : moves) {
			p.move(back);
		}
	}
		
	@Override
	public String getRedoPresentationName() {
		return "Ausrichten: " + name;
	}

	@Override
	public String getUndoPresentationName() {
		return "Ausrichten rückgängig";
	}

	
	public void addPos() {
	}

	public void newPosition(SelectableElement e, int dX, int dY) {
		e.moveElement(dX, dY);
		moves.add(new ElemPoint(e, dX, dY));
	}
}

class ElemPoint {
	protected SelectableElement e;
	private int dX;
	private int dY;

	protected ElemPoint() {
	}
	
	public ElemPoint(SelectableElement e, int dX, int dY) {
		this.e = e;
		this.dX = dX;
		this.dY = dY;
	}

	public void move(boolean back) {
		if(back) {
			e.moveElement(dX, dY);
		} else {
			e.moveElement(-dX, -dY);
		}
	}
}
