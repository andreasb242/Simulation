package gui.diagramm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;

import simulation.data.SimulationSerie;
import util.DrawHelper;

public class Diagramm extends JComponent implements DiagrammListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Farbe der Koordinatenlinien
	 */
	public final Color COORD_COLOR = Color.GRAY;

	/**
	 * Farbe der Zahlen
	 */
	public final Color NUMBER_COLOR = Color.BLACK;

	private DiagrammControl control;

	private DiagrammRenderer renderer;

	public Diagramm(DiagrammControl control) {
		this.control = control;
		
		renderer = new LineRenderer(control.getTransform());

		control.addListener(this);

		// den Fokus für die Tastendrücke hohlen
		addMouseListener(new MouseAdapter() {

			public void mouseEntered(MouseEvent e) {
				requestFocus();
			}

		});

		// Auf Scrollen reagieren
		addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				// scale.zoom(e.getWheelRotation());
			}
		});

		// Auf + und - und Pfeiltasten
		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 37:
					left();
					break;
				case 39:
					right();
					break;
				case 38:
					up();
					break;
				case 40:
					down();
					break;
				}
			}

			public void keyTyped(KeyEvent e) {
				switch (e.getKeyChar()) {
				case '+':
					// scale.zoomInH();
					break;
				case '-':
					// scale.zoomOutH();
					break;
				}
			}

		});

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				sizeChanged();
			}

		});
	}

	private void sizeChanged() {
		int width = getWidth();
		int height = getHeight();

		control.setSize(width, height);
		repaint();
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		DrawHelper.antialisingOn(g);

		int width = (int) getBounds().getWidth();
		int height = (int) getBounds().getHeight();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		g.setColor(Color.BLACK);

		control.getCoordinates().paint(g);

		renderer.renderChart(g, control.getSeries());
	}

	public void up() {
		// TODO: implementieren
		System.out.println("up");
	}

	public void down() {
		// TODO: implementieren
		System.out.println("down");
	}

	public void left() {
		// TODO: implementieren
		System.out.println("left");
	}

	public void right() {
		// TODO: implementieren
		System.out.println("right");
	}

	public void gotoZero() {
		// TODO: implementieren
		System.out.println("zero");
	}

	@Override
	public void visibilityChanged(SimulationSerie serie) {
		repaint();
	}

	@Override
	public void scaleChanged() {
		sizeChanged();
		renderer.setTransform(control.getTransform());
	}
}
