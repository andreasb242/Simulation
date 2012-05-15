package ch.zhaw.simulation.xyviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import ch.zhaw.simulation.editor.xy.element.meso.MesoImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.xyviewer.model.XYResultList;
import ch.zhaw.simulation.xyviewer.model.XYResultStepEntry;
import ch.zhaw.simulation.xyviewer.model.XYResultStepList;

public class XYViewer {
	private Dimension size;
	private MesoImage[] images;
	private GuiConfig config = new GuiConfig();
	private int width;

	public XYViewer() {
	}

	public void init(XYResultList result) {
		this.size = result.getModelSize();
		Color[] colors = result.getColors();
		images = new MesoImage[colors.length];
		this.width = config.getMesoSize();

		for (int i = 0; i < colors.length; i++) {
			MesoImage m = new MesoImage(this.width, config);
			m.setColor(colors[i]);
			images[i] = m;
		}
	}

	public void draw(Graphics2D g, XYResultStepList stepList) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);

		for (XYResultStepEntry stepEntry : stepList) {
			drawMeso(g, stepEntry);
		}
	}

	private void drawMeso(Graphics2D g, XYResultStepEntry stepEntry) {
		if (stepEntry == null) System.out.println("stepEntry == null");
		if (stepEntry.getResultEntry() == null) System.out.println("stepEntry.getResultEntry() == null");

		if (stepEntry.getResultEntry().getColorId() > this.images.length) {
			System.out.println(stepEntry.getResultEntry().getColorId());
			return;
		}

		MesoImage image = this.images[stepEntry.getResultEntry().getColorId()];

		Graphics2D g2 = (Graphics2D) g.create(stepEntry.getX(), stepEntry.getY(), this.width, this.width);
		image.drawImage(g2, false);
		g2.dispose();
	}

}
