package ch.zhaw.simulation.model.selection;

public interface SelectableElement {
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	
	public void moveElement(int dX, int dY);
}
