package ch.zhaw.simulation.xyviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import ch.zhaw.simulation.editor.xy.element.meso.MesoImage;
import ch.zhaw.simulation.plugin.data.XYDensityRaw;
import ch.zhaw.simulation.plugin.data.XYResultList;
import ch.zhaw.simulation.plugin.data.XYResultStepEntry;
import ch.zhaw.simulation.plugin.data.XYResultStepList;
import ch.zhaw.simulation.sysintegration.GuiConfig;

public class XYViewer extends JComponent {
	private static final long serialVersionUID = 1L;

	// list of mesos with positions (XYResultStepEntry) at any time and
	// submodel-results (SimulationCollection)
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

	private PositionModel model;

	private PositionListener listener = new PositionListener() {

		@Override
		public void positionChanged(int pos) {
			setPostion(pos);
		}
	};

	public XYViewer(XYResultList resultList, Vector<XYDensityRaw> rawList, PositionModel model) {
		setPreferredSize(resultList.getModelSize());
		this.resultList = resultList;
		this.model = model;
		this.rawMap = new HashMap<String, XYDensityRaw>();
		for (XYDensityRaw raw : rawList) {
			rawMap.put(raw.getDensityName(), raw);
		}

		this.model.addListener(listener);

		setFocusable(true);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocus();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					XYViewer.this.model.positionPrev();
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					XYViewer.this.model.positionNext();
				} else if (e.getKeyCode() == KeyEvent.VK_HOME) {
					XYViewer.this.model.positionStart();
				} else if (e.getKeyCode() == KeyEvent.VK_END) {
					XYViewer.this.model.positionEnd();
				}
			}
		});

		rawCurrent = null;

		// draw first frame
		stepList = resultList.getStep(0);

		this.size = resultList.getModelSize();
		Color[] colors = resultList.getColors();
		images = new MesoImage[colors.length];
		radius = config.getMesoSize();
		halfRadius = radius / 2;

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

	public void setPostion(int pos) {
		stepList = resultList.getStep(pos);
		repaint();
	}

	public void setDensitySelected(XYDensityRaw raw) {
		this.rawCurrent = raw;
		repaint();
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
		if (stepEntry == null)
			System.out.println("XYViewer.drawMeso() stepEntry == null");
		if (stepEntry.getResultEntry() == null)
			System.out.println("XYViewer.drawMeso() stepEntry.getResultEntry() == null");

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

	public void dispose() {
		this.model.removeListener(listener);
	}
}
