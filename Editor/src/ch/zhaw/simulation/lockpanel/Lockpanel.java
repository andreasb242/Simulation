package ch.zhaw.simulation.lockpanel;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXBusyLabel;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.window.LockProgressbar;

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
	 * Cancel / Schliessen Image
	 */
	private static BufferedImage closeImg;

	/**
	 * Die Animation
	 */
	private JXBusyLabel busyLabel;

	/**
	 * Die Progressbar
	 */
	private LockProgressbar progress = new LockProgressbar();

	/**
	 * Der Text mit dem Status
	 */
	private JLabel lbText = new JLabel();

	/**
	 * Zur Layoutverwaltung
	 */
	private GridBagManager gbm;

	/**
	 * Parent for repaint fix because of transparency
	 */
	private Container parent;

	/**
	 * Or close / cancel Button
	 */
	private JLabel btCancel;

	/**
	 * Bild laden
	 */
	static {
		try {
			img = ImageIO.read(Lockpanel.class.getResource("overlay.png"));
		} catch (IOException e) {
			Errorhandler.showError(e, "Overlay für Lock konnte nicht geladen werden!");
		}
		try {
			closeImg = ImageIO.read(Lockpanel.class.getResource("close.png"));
		} catch (IOException e) {
			Errorhandler.showError(e, "Close Button konnte nicht geladen werden");
		}
	}

	/**
	 * Konstruktor
	 * 
	 * @param parent
	 *            Der parent Component, wegen der Halptransparenz muss er neu
	 *            gezeichnet werden, wird nicht automatisch von Java vorgenommen
	 */
	public Lockpanel(final Container parent, final ActionListener cancelListener) {
		this.parent = parent;

		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		gbm = new GridBagManager(this);

		btCancel = new JLabel(new ImageIcon(closeImg));
		btCancel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				cancelListener.actionPerformed(null);
			}

		});

		busyLabel = new JXBusyLabel(new Dimension(40, 40)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void frameChanged() {
				Rectangle b = busyLabel.getBounds();
				parent.repaint(b.x, b.y, b.width, b.height);
				super.frameChanged();
			}
		};

		busyLabel.setOpaque(false);

		gbm.setX(0).setWidth(2).setY(0).setInsets(new Insets(20, 10, 10, 20)).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.FIRST_LINE_END)
				.setComp(btCancel);

		gbm.setX(0).setWidth(2).setY(0).setInsets(new Insets(35, 10, 10, 10)).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER)
				.setComp(busyLabel);

		gbm.setX(0).setWidth(2).setY(1).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER).setComp(progress);

		lbText.setForeground(Color.WHITE);

		gbm.setX(0).setWidth(2).setY(10).setInsets(new Insets(10, 10, 35, 10)).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.CENTER)
				.setComp(lbText);

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
		progress.setVisible(flag);
		progress.setPercent(0, null);
	}

	/**
	 * Sets the progress in Percent, -1 hides the Progressbar
	 */
	public void setPercent(int percent) {
		progress.setPercent(percent, parent);
	}

	public void setCancelable(boolean b) {
		btCancel.setVisible(b);
	}
}
