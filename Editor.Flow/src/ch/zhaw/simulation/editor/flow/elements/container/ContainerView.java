package ch.zhaw.simulation.editor.flow.elements.container;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.flow.element.SimulationContainerData;

public class ContainerView extends GuiDataTextElement<SimulationContainerData> {
	private static final long serialVersionUID = 1L;
	private ContainerImage image;
	
	public ContainerView(int width, int heigth, FlowEditorControl control, SimulationContainerData o) {
		super(o, control);
		
		image = new ContainerImage(width, heigth, control.getSysintegration().getGuiConfig());
		
		textY = 45;
		questionmarkY = 60;
	}
	
	@Override
	protected GuiImage getImage() {
		return image;
	}
}
