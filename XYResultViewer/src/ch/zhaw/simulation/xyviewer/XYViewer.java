package ch.zhaw.simulation.xyviewer;

import java.awt.*;

import ch.zhaw.simulation.editor.xy.element.meso.MesoImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.xyviewer.model.XYResultList;
import ch.zhaw.simulation.xyviewer.model.XYResultStepEntry;
import ch.zhaw.simulation.xyviewer.model.XYResultStepList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class XYViewer extends JComponent implements ChangeListener {
	private XYResultList resultList;
	XYResultStepList stepList;
	private Dimension size;
	private MesoImage[] images;
	private GuiConfig config = new GuiConfig();

	// radius of a meso compartiment
	private int radius;
	private int halfRadius;

	public XYViewer(XYResultList resultList) {
		setPreferredSize(resultList.getModelSize());

		this.resultList = resultList;

		// draw first frame
		stepList = resultList.getStep(0);

		this.size = resultList.getModelSize();
		Color[] colors = resultList.getColors();
		images = new MesoImage[colors.length];
		radius = config.getMesoSize();
		halfRadius = radius/2;

		for (int i = 0; i < colors.length; i++) {
			MesoImage m = new MesoImage(radius, config);
			m.setColor(colors[i]);
			images[i] = m;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		draw((Graphics2D) g, stepList);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		stepList = resultList.getStep(slider.getValue());
		repaint();
	}

	public void draw(Graphics2D g, XYResultStepList stepList) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);

		for (XYResultStepEntry stepEntry : stepList) {
			drawMeso(g, stepEntry);
		}
	}

	private void drawMeso(Graphics2D g, XYResultStepEntry stepEntry) {
		if (stepEntry == null) System.out.println("XYViewer.drawMeso() stepEntry == null");
		if (stepEntry.getResultEntry() == null) System.out.println("XYViewer.drawMeso() stepEntry.getResultEntry() == null");

		if (stepEntry.getResultEntry().getColorId() > this.images.length) {
			System.out.println(stepEntry.getResultEntry().getColorId());
			return;
		}

		MesoImage image = this.images[stepEntry.getResultEntry().getColorId()];

		Graphics2D g2 = (Graphics2D) g.create(stepEntry.getX() - halfRadius, stepEntry.getY() - halfRadius, radius, radius);
		image.drawImage(g2, false);
		g2.dispose();
	}

}
