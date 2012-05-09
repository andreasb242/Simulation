package ch.zhaw.simulation.window;

import java.awt.GridBagConstraints;
import java.awt.image.BufferedImageOp;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.effect.LayerEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.swingx.image.FastBlurFilter;

import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.lockpanel.Lockpanel;

public class LockFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * The overlay panel with the "spinning circle"
	 */
	private Lockpanel lock;

	/**
	 * UI um en Dialog unscharf darzustellen wenn Daten geladen werden
	 */
	private LockableUI blurUI;

	/**
	 * Layer mit den Komponenten
	 */
	private JXLayer<JComponent> layer;

	/**
	 * Das Panel mit allen Controls, damit es beim laden verschwommen
	 * dargestellt werden kann...
	 */
	private JPanel panel = new JPanel();

	public LockFrame() {

		// Debugging NICHT aktivieren, es werden mehrere Komponenten
		// übereinander
		// gelegt! (Halbtransparentes JPanel (Wartedialog bei Datenladen)
		GridBagManager gbm = new GridBagManager(this, false);

		blurUI = new LockableUI(new LayerEffect[] { new BufferedImageOpEffect(new BufferedImageOp[] { new FastBlurFilter(2) }) });

		layer = new JXLayer<JComponent>(panel);

		layer.setUI(blurUI);

		lock = new Lockpanel(this);

		gbm.setX(1).setY(1).setFill(GridBagConstraints.NONE).setComp(lock);

		gbm.setX(0).setY(0).setWidth(3).setHeight(3).setComp(layer);
	}

	/**
	 * @return The panel with the contents
	 */
	public JPanel getPanel() {
		return panel;
	}

	public void unlock() {
		lock.setVisible(false);
		blurUI.setLocked(false);

		enableJmenu(true);
	}
	
	/**
	 * Sets the progress in Percent, -1 hides the Progressbar
	 */
	public void setPercent(int percent) {
		lock.setPercent(percent);
	}

	private void enableJmenu(boolean enable) {
		JMenuBar menu = getJMenuBar();
		if (menu == null) {
			return;
		}

		for (int i = 0; i < menu.getMenuCount(); i++) {
			JMenu m = menu.getMenu(i);
			m.setEnabled(enable);
		}
	}

	public void lock(String text) {
		blurUI.setLocked(true);
		lock.setVisible(true);
		lock.setText(text);

		enableJmenu(false);
	}

	public void setLockText(String text) {
		lock.setText(text);
	}

	public static void main(String[] args) {
		final LockFrame f = new LockFrame();
		f.setSize(200, 200);
		f.setVisible(true);

		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			private int x = 0;

			@Override
			public void run() {
				if (x == 0) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							f.lock("Test123");
						}
					});
				} else if (x == 1) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							f.unlock();
						}
					});
				} else {
					t.cancel();
				}

				x++;
			}
		}, 5000, 5000);

	}

}
