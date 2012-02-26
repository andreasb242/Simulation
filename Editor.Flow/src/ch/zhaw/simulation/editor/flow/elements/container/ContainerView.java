package ch.zhaw.simulation.editor.flow.elements.container;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.gui.control.FlowEditorControl;
import ch.zhaw.simulation.model.flow.element.SimulationContainer;

public class ContainerView extends GuiDataTextElement<SimulationContainer> {
	private static final long serialVersionUID = 1L;
	private ContainerImage image;
	
	public ContainerView(int width, int heigth, FlowEditorControl control, SimulationContainer o) {
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
