package ch.zhaw.simulation.diagram.sidebar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

public class SerieTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {
	private static final long serialVersionUID = 1L;

	private ColorCheckbox cb = new ColorCheckbox();

	public SerieTreeCellEditor() {
		cb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	@Override
	public Object getCellEditorValue() {
		return cb.isSelected();
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		cb.setName(value.toString());

		if (value instanceof SerieTreeNode) {
			SerieTreeNode st = (SerieTreeNode) value;
			cb.setSelected(st.isSerieVisible());
			cb.setColorAndStroke(st.getColor(), st.getStroke());
		}

		return cb;
	}

}
