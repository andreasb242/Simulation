package ch.zhaw.simulation.window;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import butti.javalibs.util.DrawHelper;

public class LockProgressbar extends JComponent {
	private static final long serialVersionUID = 1L;

	private int percent = -1;

	public LockProgressbar() {
		setPreferredSize(new Dimension(200, 25));
	}

	@Override
	public void paint(Graphics g1) {
		if (percent < 0) {
			return;
		}

		Graphics2D g = DrawHelper.antialisingOn(g1);

		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.setPaint(Color.WHITE);

		int w = (getWidth() - 2) * percent / 100;
		g.fillRect(1, 1, w, getHeight() - 2);
	}

	public void setPercent(int percent, Container parent) {
		int lastPercent = this.percent;
		this.percent = percent;

		if (lastPercent < 0 || lastPercent > percent) {
			if (parent != null) {
				Rectangle b = getBounds();
				parent.repaint(b.x, b.y, b.width, b.height);
			} else {
				repaint();
			}
		} else {
			int w = (getWidth() - 2) * percent / 100;
			repaint(0, 0, w, getHeight());
		}
	}
}
