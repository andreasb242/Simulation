package ch.zhaw.simulation.util.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.UIManager;

public class HeaderPanel extends JPanel {
	private static final long serialVersionUID = 1L;

    public void paintComponent(Graphics g1) {
        Color control = UIManager.getColor("control");
        int width = getWidth();
        int height = getHeight();

        Graphics2D g = (Graphics2D) g1;
        g.setPaint(new GradientPaint(100, 0, Color.WHITE, width, 0, control));
        g.fillRect(0, 0, width, height);
        
        int h = height - 1;
        g.setColor(new Color(0x5aafff));
        g1.drawLine(0, h, width, h);
    }

}
