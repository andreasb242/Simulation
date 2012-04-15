package ch.zhaw.simulation.xyviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import ch.zhaw.simulation.editor.xy.element.meso.MesoImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.xyviewer.model.XYResult;
import ch.zhaw.simulation.xyviewer.model.XYResultMeso;
import ch.zhaw.simulation.xyviewer.model.XYResultStep;

public class XYViewer {
	private Dimension size;
	private MesoImage[] images;
	private GuiConfig config = new GuiConfig();
	private int width;

	public XYViewer() {
	}

	public void init(XYResult result) {
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

	public void draw(Graphics2D g, XYResultStep step) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);

		for (int i = 0; i < step.getMesoCount(); i++) {
			drawMeso(g, step.getMeso(i));
		}
	}

	private void drawMeso(Graphics2D g, XYResultMeso meso) {
		MesoImage image = this.images[meso.getColorId()];

		Graphics2D g2 = (Graphics2D) g.create(meso.getX(), meso.getY(), this.width, this.width);
		image.drawImage(g2, false);
		g2.dispose();
	}

}
