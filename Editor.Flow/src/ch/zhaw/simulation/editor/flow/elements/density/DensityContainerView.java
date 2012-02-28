package ch.zhaw.simulation.editor.flow.elements.density;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainer;

public class DensityContainerView extends GuiDataTextElement<SimulationDensityContainer> {
	private static final long serialVersionUID = 1L;
	private DensityContainerImage image;

	public DensityContainerView(int width, int heigth, FlowEditorControl control, SimulationDensityContainer o) {
		super(o, control);

		image = new DensityContainerImage(width, heigth, control.getSysintegration().getGuiConfig());

		textY = 45;
		questionmarkY = 60;
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}
}
