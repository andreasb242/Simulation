package ch.zhaw.simulation.undo.debug;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.undo.UndoableEdit;

import butti.javalibs.controls.TitleLabel;

public class UndoRedoListCellRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;

	private Color COLOR_UNDONE = new Color(0xFFAFAB);

	private JLabel txtUndo = new JLabel();
	private JLabel txtRedo = new JLabel();
	private JLabel txtClass = new TitleLabel("");

	public UndoRedoListCellRenderer() {
		txtUndo.setForeground(Color.BLACK);
		txtRedo.setForeground(Color.BLACK);
		txtClass.setForeground(Color.BLACK);

		setLayout(new GridLayout(0, 1));
		add(txtClass);
		add(txtUndo);
		add(txtRedo);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		boolean undone = false;

		if (value instanceof UndoableEdit) {
			UndoableEdit ue = (UndoableEdit) value;
			txtClass.setText(ue.getClass().getName());
			txtUndo.setText(ue.getUndoPresentationName());
			txtRedo.setText(ue.getRedoPresentationName());
			if (ue.canRedo()) {
				undone = true;
			}
		} else {
			txtUndo.setText("Class:");
			txtRedo.setText(value.getClass().getName());
		}

		Color color = null;
		if (undone) {
			color = COLOR_UNDONE;
		} else {
			color = Color.WHITE;
		}

		if (index % 2 == 0) {
			color = color.darker();
		}
		setBackground(color);

		return this;
	}

}
