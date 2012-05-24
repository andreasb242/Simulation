package ch.zhaw.simulation.undo.debug;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import butti.javalibs.config.WindowPositionSaver;

import ch.zhaw.simulation.undo.UndoHandler;

public class UndoRedoDebugDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JList list;
	private UndoListModel model;

	public UndoRedoDebugDialog(UndoHandler undoHandler) {
		setTitle("Undo / Redo Testdialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		model = new UndoListModel(undoHandler);
		list = new JList(model);
		list.setCellRenderer(new UndoRedoListCellRenderer());

		add(new JScrollPane(list));

		new WindowPositionSaver(this);
	}

	@Override
	public void dispose() {
		model.dispose();
		super.dispose();
	}

}
