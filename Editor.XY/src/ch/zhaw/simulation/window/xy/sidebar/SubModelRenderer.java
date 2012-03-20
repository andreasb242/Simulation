package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import ch.zhaw.simulation.model.xy.SubModel;
import ch.zhaw.simulation.xy.util.ColorIcon;

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
}
