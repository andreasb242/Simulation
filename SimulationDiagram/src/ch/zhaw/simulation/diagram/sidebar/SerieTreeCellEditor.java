package ch.zhaw.simulation.diagram.sidebar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

public class SerieTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {
	private static final long serialVersionUID = 1L;

	private SerieTreeCellRenderer renderer;

	public SerieTreeCellEditor() {
		this.renderer = new SerieTreeCellRenderer();

		renderer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	@Override
	public Object getCellEditorValue() {
		return renderer.isSelected();
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		return renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
	}
}
