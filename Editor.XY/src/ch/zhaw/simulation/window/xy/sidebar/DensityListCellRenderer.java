package ch.zhaw.simulation.window.xy.sidebar;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.JXLabel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.layout.VerticalMaxWidthLayout;
import ch.zhaw.simulation.model.xy.DensityData;

public class DensityListCellRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;

	private JLabel lbName = new TitleLabel(" ");
	private JXLabel lbDescription = new JXLabel(" ");

	public DensityListCellRenderer() {
		setOpaque(true);
		setLayout(new VerticalMaxWidthLayout(200));

		add(lbName);
		add(lbDescription);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value instanceof DensityData) {
			DensityData d = (DensityData) value;
			lbName.setText(d.getName());
			lbDescription.setText(d.getDescription());
		} else {
			lbName.setText(" ");
			lbDescription.setText(" ");
		}

		return this;
	}
}
