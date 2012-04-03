package clipboard;

import ch.zhaw.simulation.model.selection.SelectableElement;

public class TestSelectable implements SelectableElement<Object> {

	private Object data;

	public TestSelectable(Object data) {
		this.data = data;
	}
	
	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void moveElement(int dX, int dY) {
	}

	@Override
	public Object getData() {
		return data;
	}

}
