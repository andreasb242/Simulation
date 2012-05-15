package ch.zhaw.simulation.diagram.sidebar;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorCheckbox extends JPanel {
	private static final long serialVersionUID = 1L;

	private Color color = Color.BLACK;
	private BasicStroke stroke = null;

	private JLabel label;
	private JCheckBox cb;

	private Icon icon = new Icon() {

		@Override
		public void paintIcon(Component c, Graphics g1, int x, int y) {
			Graphics2D g = (Graphics2D) g1;
			g.setColor(color);
			g.setStroke(stroke);

			int yM = getIconHeight() / 2 + y;

			g.drawLine(x, yM, getIconWidth(), yM);
		}

		@Override
		public int getIconWidth() {
			return 32;
		}

		@Override
		public int getIconHeight() {
			return 32;
		}
	};

	public ColorCheckbox() {
		setLayout(new BorderLayout());
		cb = new JCheckBox();
		add(cb, BorderLayout.CENTER);
		setOpaque(false);

		label = new JLabel(icon);
		add(label, BorderLayout.WEST);
	}

	public void setName(String name) {
		cb.setText(name);
	}

	public void setColorAndStroke(Color c, BasicStroke stroke) {
		this.color = c;
		this.stroke = stroke;
		label.repaint();
	}

	@Override
	public void setForeground(Color fg) {
		if (cb != null) {
			cb.setForeground(fg);
		}
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
