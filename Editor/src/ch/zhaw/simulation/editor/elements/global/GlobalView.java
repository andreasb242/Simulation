package ch.zhaw.simulation.editor.elements.global;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.element.SimulationGlobal;

public class GlobalView extends GuiDataTextElement<SimulationGlobal> {
	private static final long serialVersionUID = 1L;
	private GlobalImage image;

	public GlobalView(int size, AbstractEditorControl<?> control, SimulationGlobal data) {
		super(data, control);

		image = new GlobalImage(size, control.getSysintegration().getGuiConfig());

		textY = 30;
		questionmarkY = 45;
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}

}
