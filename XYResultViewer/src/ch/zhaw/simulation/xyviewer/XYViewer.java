package ch.zhaw.simulation.xyviewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ch.zhaw.simulation.editor.xy.element.meso.MesoImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;
import ch.zhaw.simulation.plugin.data.XYResultStepEntry;
import ch.zhaw.simulation.plugin.data.XYResultStepList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class XYViewer extends JComponent implements ChangeListener, ActionListener {
	private ResultViewerDialog dialog;

	// list of mesos with positions (XYResultStepEntry) at any time and submodel-results (SimulationCollection)
	private XYResultList resultList;

	// list of meso-positions at a specific time
	private XYResultStepList stepList;

	// list of densities at the end of the simulation
	private Map<String, XYDensityRaw> rawMap;
	private XYDensityRaw rawCurrent;

	private Dimension size;
	private MesoImage[] images;
	private GuiConfig config = new GuiConfig();

	// radius of a meso compartiment
	private int radius;
	private int halfRadius;

	public XYViewer(ResultViewerDialog dialog, XYResultList resultList, Vector<XYDensityRaw> rawList) {
		setPreferredSize(resultList.getModelSize());
		this.dialog = dialog;
		this.resultList = resultList;
		this.rawMap = new HashMap<String, XYDensityRaw>();
		for (XYDensityRaw raw : rawList) {
			rawMap.put(raw.getDensityName(), raw);
		}
		rawCurrent = null;

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

	/**
	 * Callback function
	 * Gets called every time the slider changes its value
	 *
	 * @param e
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		stepList = resultList.getStep(slider.getValue());
		repaint();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JRadioButton) {
			JRadioButton radioButton = (JRadioButton) e.getSource();
			rawCurrent = rawMap.get(radioButton.getText());
			repaint();
		}
	}

	public void draw(Graphics2D g, XYResultStepList stepList) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);

		if (rawCurrent != null) {
			drawDensity(g);
		}

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

	private void drawDensity(Graphics2D g) {
		BufferedImage img;
		int[] pixels;
		int rgb;
		int tmp;
		double value;
		double maxMinusFactor;
		double maxPlusFactor;

		img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

		maxMinusFactor = 255.0 / rawCurrent.getMaxMinus();
		maxPlusFactor = 255.0 / rawCurrent.getMaxPlus();

		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++) {
				value = rawCurrent.getMatrixValue(x, y);
				rgb = 0xffffff;

				if (value < -0.1) {
					tmp = 255 - (int) (value * maxMinusFactor);
					rgb = (tmp << 16) | (tmp << 8) | 0x0000ff;
				} else if (value > 0.1) {
					tmp = 255 - ((int) (value * maxPlusFactor));
					rgb = (tmp << 8) | tmp | 0xff0000;
				}

				pixels[x + y * size.width] = rgb;
			}
		}

		g.drawImage(img, 0, 0, null);
	}

}
