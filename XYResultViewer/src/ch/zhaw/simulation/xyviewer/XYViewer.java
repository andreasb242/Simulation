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
import java.util.Vector;

import javax.swing.JComponent;

import butti.javalibs.errorhandler.Errorhandler;
import ch.zhaw.simulation.densitydraw.DensityRenderer;
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

	private XYDensityRaw rawCurrent;

	private MesoImage[] images;
	private GuiConfig config = new GuiConfig();

	// radius of a meso compartiment
	private int radius;
	private int halfRadius;

	private PositionModel model;

	private BufferedImage densityImg;
	private BufferedImage lastDensityImg;

	private DensityRenderer renderer = new DensityRenderer() {

		@Override
		protected boolean isLogarithmic() {
			return rawCurrent.isLogView();
		}

		@Override
		protected double getValueFor(int x, int y) {
			return rawCurrent.getMatrixValue(x, y);
		}

		@Override
		protected double getMaxPlus() {
			return rawCurrent.getMaxPlus();
		}

		@Override
		protected double getMaxMinus() {
			return rawCurrent.getMaxMinus();
		}
	};

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

		renderer.setSize(resultList.getModelSize());
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
		// If there are different densities for different times call:
		// repaintDensity()
	}

	public void setSelectedDensity(XYDensityRaw raw) {
		this.rawCurrent = raw;
		repaintDensity();
	}

	public void repaintDensity() {
		this.densityImg = null;
		repaint();
	}

	public XYDensityRaw getSelectedDensity() {
		return this.rawCurrent;
	}

	public void draw(Graphics2D g, XYResultStepList stepList) {
		g.setColor(Color.WHITE);
		Dimension size = renderer.getSize();
		g.fillRect(0, 0, size.width, size.height);

		if (rawCurrent != null) {
			drawDensity(g);
		}

		for (XYResultStepEntry stepEntry : stepList) {
			drawMeso(g, stepEntry);
		}
	}

	private void drawMeso(Graphics2D g, XYResultStepEntry stepEntry) {
		if (stepEntry == null) {
			System.err.println("XYViewer.drawMeso() stepEntry == null");
			return;
		}
		if (stepEntry.getResultEntry() == null) {
			System.out.println("XYViewer.drawMeso() stepEntry.getResultEntry() == null");
			return;
		}

		if (stepEntry.getResultEntry().getColorId() > this.images.length) {
			return;
		}

		MesoImage image = this.images[stepEntry.getResultEntry().getColorId()];

		Graphics2D g2 = (Graphics2D) g.create(stepEntry.getX() - halfRadius, stepEntry.getY() - halfRadius, radius, radius);
		image.drawImage(g2, false);
		g2.dispose();
	}

	private void drawDensity(Graphics2D g) {
		try {
			if (densityImg == null) {
				densityImg = renderer.drawDensityColor(lastDensityImg);
				lastDensityImg = densityImg;
			}
			g.drawImage(densityImg, 0, 0, null);
		} catch (Exception e) {
			// here should nothing happen, because the data is read, and not
			// calculated
			Errorhandler.showError(e);
		}
	}

	public void dispose() {
		this.model.removeListener(listener);
	}
}
