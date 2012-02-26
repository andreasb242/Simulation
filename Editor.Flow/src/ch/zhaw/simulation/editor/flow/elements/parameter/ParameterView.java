package ch.zhaw.simulation.editor.flow.elements.parameter;

import ch.zhaw.simulation.editor.flow.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.SimulationParameter;

public class ParameterView extends GuiDataTextElement<SimulationParameter> {
	private static final long serialVersionUID = 1L;
	private ParameterImage image;

	public ParameterView(int size, FlowEditorControl control, SimulationParameter data) {
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
