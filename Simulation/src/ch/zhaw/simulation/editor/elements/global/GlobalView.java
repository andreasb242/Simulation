package ch.zhaw.simulation.editor.elements.global;

import ch.zhaw.simulation.editor.elements.GuiDataTextElement;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.SimulationGlobal;

public class GlobalView extends GuiDataTextElement<SimulationGlobal> {
	private static final long serialVersionUID = 1L;
	private GlobalImage image;

	public GlobalView(int size, SimulationControl control, SimulationGlobal data) {
		super(data, control);

		image = new GlobalImage(size, control.getConfig());

		textY = 30;
		questionmarkY = 45;
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}

}
