package ch.zhaw.simulation.editor.flow.elements.global;

import ch.zhaw.simulation.editor.flow.elements.GuiDataTextElement;
import ch.zhaw.simulation.editor.flow.elements.GuiImage;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.SimulationGlobal;

public class GlobalView extends GuiDataTextElement<SimulationGlobal> {
	private static final long serialVersionUID = 1L;
	private GlobalImage image;

	public GlobalView(int size, FlowEditorControl control, SimulationGlobal data) {
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
