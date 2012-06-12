package ch.zhaw.simulation.editor.xy.element.meso;

import java.awt.event.MouseEvent;

import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.view.GuiDataTextElement;
import ch.zhaw.simulation.editor.xy.XYEditorControl;
import ch.zhaw.simulation.model.xy.MesoData;
import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class MesoView extends GuiDataTextElement<MesoData> {
	private static final long serialVersionUID = 1L;
	private MesoImage image;

	public MesoView(XYEditorControl control, MesoData data) {
		super(data, control);
		GuiConfig guiconf = control.getSysintegration().getGuiConfig();
		this.image = new MesoImage(guiconf.getMesoSize(), guiconf);

		dataChanged();

		this.textY = 20;
		this.questionmarkY = 30;
	}

	@Override
	protected void doubleClicked(MouseEvent e) {
		SubModel submodel = getData().getSubmodel();
		if (submodel == null) {
			Messagebox.showInfo(getControl().getParent(), "Kein Modell",
					"<html><b>Bitte ordnen Sie in der Sidebar dem Kompartment ein Modell zu, damit Sie dieses bearbeiten können.</b><br><br>"
							+ "Sie finden in der Toolbar direkt neben dem Meso Button ein kleiner Pfeil,<br>"
							+ "dort können Sie Submodelle erstellen, wählen und verwalten." + "</html>");
		}
	}

	@Override
	public void dataChanged() {
		if (getData().getSubmodel() != null) {
			this.image.setColor(getData().getSubmodel().getColor());
		}
		repaint();
	}

	@Override
	protected GuiImage getImage() {
		return image;
	}
}
