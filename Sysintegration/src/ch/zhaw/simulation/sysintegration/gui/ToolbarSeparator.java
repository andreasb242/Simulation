package ch.zhaw.simulation.sysintegration.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class ToolbarSeparator extends JComponent {
	private static final long serialVersionUID = 1L;

	public ToolbarSeparator() {
		setForeground(Color.GRAY);
		
		setPreferredSize(new Dimension(10, 24));
		setSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
	}
	
	@Override
	public void paint(Graphics g) {
		int x = getWidth() / 2;
		g.drawLine(x, 0, x, 24);
	}
}
