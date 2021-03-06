package ch.zhaw.simulation.editor.flow.elements.density;

import java.awt.event.MouseEvent;

import ch.zhaw.simulation.control.flow.FlowEditorControl;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.model.flow.element.SimulationDensityContainerData;

public class DensityContainerView extends GuiDataTextElement<SimulationDensityContainerData> {
	private static final long serialVersionUID = 1L;
	private DensityContainerImage image;

	public DensityContainerView(int width, int heigth, FlowEditorControl control, SimulationDensityContainerData o) {
		super(o, control);

		image = new DensityContainerImage(width, heigth, control.getSysintegration().getGuiConfig());

		textY = 45;
		questionmarkY = 60;
	}


	@Override
	protected void doubleClicked(MouseEvent e) {
		// nothing to edit, so nothing to do here
	}
	
	@Override
	protected GuiImage getImage() {
		return image;
	}
}
