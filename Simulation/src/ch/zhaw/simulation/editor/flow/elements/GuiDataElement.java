package ch.zhaw.simulation.editor.flow.elements;


import java.awt.Dimension;

import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.flow.SimulationObject;


public abstract class GuiDataElement<T extends SimulationObject> extends GuiElement {
	private static final long serialVersionUID = 1L;
	private T data;

	public GuiDataElement(T data, SimulationControl control) {
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
