package ch.zhaw.simulation.editor.xy.element;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.model.xy.AtomData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class AtomView extends GuiDataTextElement<AtomData> {
	private static final long serialVersionUID = 1L;
	private AtomImage image;

	public AtomView(XYEditorControl control, AtomData data) {
		super(data, control);
		GuiConfig guiconf = control.getSysintegration().getGuiConfig();
		this.image = new AtomImage(guiconf.getAtomSize(), guiconf);

		this.textY = 20;
		this.questionmarkY = 35;
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}
}
