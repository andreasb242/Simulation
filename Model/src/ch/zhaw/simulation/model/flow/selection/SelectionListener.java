package ch.zhaw.simulation.model.flow.selection;

public interface SelectionListener {
	public void selectionChanged();

	public void selectionMoved(int dX, int dY);
}
