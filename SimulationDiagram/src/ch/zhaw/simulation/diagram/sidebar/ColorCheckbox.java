package ch.zhaw.simulation.diagram.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorCheckbox extends JPanel {
	private static final long serialVersionUID = 1L;

	private Color color = Color.BLACK;

	private JLabel label;
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

	public ColorCheckbox() {
		setLayout(new BorderLayout());
		cb = new JCheckBox();
		add(BorderLayout.CENTER, cb);
		setOpaque(false);

		label = new JLabel(icon);
		add(BorderLayout.WEST, label);
	}

	public void setName(String name) {
		cb.setText(name);
	}

	public void setColor(Color c) {
		this.color = c;
		label.repaint();
	}

	public void addActionListener(ActionListener listener) {
		cb.addActionListener(listener);
	}

	public void addItemListener(ItemListener listener) {
		cb.addItemListener(listener);
	}

	public boolean isEnabled() {
		return cb.isEnabled();
	}

	public boolean isSelected() {
		return cb.isSelected();
	}

	public void setSelected(boolean selected) {
		cb.setSelected(selected);
	}
}
