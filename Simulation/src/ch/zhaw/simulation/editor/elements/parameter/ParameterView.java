package ch.zhaw.simulation.editor.elements.parameter;

import ch.zhaw.simulation.editor.elements.GuiDataTextElement;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.gui.control.SimulationControl;
import ch.zhaw.simulation.model.SimulationParameter;

public class ParameterView extends GuiDataTextElement<SimulationParameter> {
	private static final long serialVersionUID = 1L;
	private ParameterImage image;

	public ParameterView(int size, SimulationControl control, SimulationParameter data) {
		super(data, control);

		image = new ParameterImage(size, control.getConfig());

		textY = 30;
		questionmarkY = 45;
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}

}
