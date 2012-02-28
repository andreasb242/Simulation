package ch.zhaw.simulation.editor.elements;

import java.awt.Dimension;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.model.element.SimulationObject;

public abstract class GuiDataElement<T extends SimulationObject> extends GuiElement {
	private static final long serialVersionUID = 1L;
	private T data;

	public GuiDataElement(T data, AbstractEditorControl<?> control) {
		super(control);
		this.data = data;

		setPreferredSize(new Dimension(data.getWidth(), data.getHeight()));
	}

	@Override
	public void dispose() {
		data = null;
		super.dispose();
	}

	public T getData() {
		return data;
	}

	@Override
	public void moveElement(int dX, int dY) {
		data.move(dX, dY);
		getModel().fireObjectChanged(data, false);
	}
}