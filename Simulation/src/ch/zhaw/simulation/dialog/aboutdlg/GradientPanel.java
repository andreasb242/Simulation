package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;

public class GradientPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (!isOpaque()) {
			return;
		}

		int width = getWidth();
		int height = getHeight();

		Graphics2D g2 = (Graphics2D) g;

		Paint storedPaint = g2.getPaint();
		g2.setPaint(new GradientPaint(0, 0, Color.WHITE, width, height,
				new Color(200, 200, 200)));
		g2.fillRect(0, 0, width, height);
		g2.setPaint(storedPaint);
	}
}
