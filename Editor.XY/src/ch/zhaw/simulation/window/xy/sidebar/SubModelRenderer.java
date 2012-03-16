package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import ch.zhaw.simulation.model.xy.SubModel;

public class SubModelRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	private ColorIcon colorIcon = new ColorIcon();

	public SubModelRenderer() {
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		SubModel m = (SubModel) value;
		if (m != null) {
			setText(m.getName());
			colorIcon.setColor(m.getColor());
			setIcon(colorIcon);
		}

		return this;
	}

	static class ColorIcon implements Icon {
		private Color color = Color.BLACK;

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillOval(x, y, getIconWidth(), getIconHeight());
		}

		public void setColor(Color color) {
			this.color = color;
		}

		@Override
		public int getIconWidth() {
			return 16;
		}

		@Override
		public int getIconHeight() {
			return 16;
		}
	}
}
