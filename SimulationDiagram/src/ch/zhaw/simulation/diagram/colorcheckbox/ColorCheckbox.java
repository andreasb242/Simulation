package ch.zhaw.simulation.diagram.colorcheckbox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorCheckbox extends JPanel {
	private static final long serialVersionUID = 1L;

	private Color color = Color.BLACK;

	private JCheckBox cb;

	private Icon icon = new Icon() {

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);

			g.fillRect(x, y, getIconWidth(), getIconHeight());
		}

		@Override
		public int getIconWidth() {
			return 4;
		}

		@Override
		public int getIconHeight() {
			return 16;
		}
	};

	private JLabel label;

	public ColorCheckbox(String name) {
		setLayout(new BorderLayout());
		cb = new JCheckBox(name);
		add(BorderLayout.CENTER, cb);

		label = new JLabel(icon);
		add(BorderLayout.WEST, label);
	}

	public void setColor(Color c) {
		this.color = c;
		label.repaint();
	}
}
