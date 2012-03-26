package ch.zhaw.simulation.lockpanel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXBusyLabel;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.GridBagManager;

/**
 * Das Overlay Panel, der schwarze, halbtransparente Rahmen
 * 
 * @author Andreas Butti
 * 
 */
public class Lockpanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Das Hingergrundbild
	 */
	private static BufferedImage img;

	/**
	 * Die Animation
	 */
	private JXBusyLabel busyLabel;

	/**
	 * Der Text mit dem Status
	 */
	private JLabel lbText = new JLabel();

	/**
	 * Zur Layoutverwaltung
	 */
	private GridBagManager gbm;

	/**
	 * Bild laden
	 */
	static {
		try {
			img = ImageIO.read(Lockpanel.class.getResource("overlay.png"));
		} catch (IOException e) {
			Errorhandler.showError(e, "Overlay für Lock konnte nicht geladen werden!");
		}
	}

	/**
	 * Konstruktor
	 * 
	 * @param parent
	 *            Der parent Component, wegen der Halptransparenz muss er neu
	 *            gezeichnet werden, wird nicht automatisch von Java vorgenommen
	 */
	public Lockpanel(final Container parent) {
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		gbm = new GridBagManager(this);

		busyLabel = new JXBusyLabel(new Dimension(40, 40)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void frameChanged() {
				Rectangle b = busyLabel.getBounds();
				parent.repaint((int) b.getX(), (int) b.getY(), (int) b.getWidth(), (int) b.getHeight());
				super.frameChanged();
			}
		};

		busyLabel.setOpaque(false);

		gbm.setX(0).setY(0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(busyLabel);

		lbText.setForeground(Color.WHITE);

		gbm.setX(0).setY(1).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(lbText);

		setVisible(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	/**
	 * Setzt den Text der Angezeigt werden soll als Status
	 * 
	 * @param text
	 *            Der Statustext
	 */
	public void setText(String text) {
		lbText.setText(text);
	}

	@Override
	public void setVisible(boolean flag) {
		super.setVisible(flag);
		busyLabel.setBusy(flag);
	}
}
