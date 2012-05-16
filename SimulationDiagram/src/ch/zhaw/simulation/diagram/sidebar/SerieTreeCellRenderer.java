package ch.zhaw.simulation.diagram.sidebar;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

public class SerieTreeCellRenderer extends SeriesCheckbox implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	public SerieTreeCellRenderer() {
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		setName(value.toString());

		if (value instanceof SerieTreeNode) {
			SerieTreeNode st = (SerieTreeNode) value;
			setSelected(st.isSerieVisible());
			setColorAndStroke(st.getColor(), st.getStroke());
		}

		return this;
	}

}
