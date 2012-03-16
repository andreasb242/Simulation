package ch.zhaw.simulation.editor.xy.element;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class MesoView extends GuiDataTextElement<MesoData> {
	private static final long serialVersionUID = 1L;
	private MesoImage image;

	public MesoView(XYEditorControl control, MesoData data) {
		super(data, control);
		GuiConfig guiconf = control.getSysintegration().getGuiConfig();
		this.image = new MesoImage(guiconf.getMesoSize(), guiconf);

		this.textY = 20;
		this.questionmarkY = 35;
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}
}
