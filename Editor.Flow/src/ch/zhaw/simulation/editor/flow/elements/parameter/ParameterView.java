package ch.zhaw.simulation.editor.flow.elements.parameter;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.flow.element.SimulationParameterData;

public class ParameterView extends GuiDataTextElement<SimulationParameterData> {
	private static final long serialVersionUID = 1L;
	private ParameterImage image;

	public ParameterView(int size, FlowEditorControl control, SimulationParameterData data) {
		super(data, control);

		image = new ParameterImage(size, control.getSysintegration().getGuiConfig());

		textY = 30;
		questionmarkY = 45;
	}
	
	@Override
	protected GuiImage getImage() {
		return image;
	}

}
