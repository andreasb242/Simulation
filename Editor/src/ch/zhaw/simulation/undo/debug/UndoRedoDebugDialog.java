package ch.zhaw.simulation.undo.debug;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import butti.javalibs.config.WindowPositionSaver;

import ch.zhaw.simulation.undo.UndoHandler;

public class UndoRedoDebugDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JList list;
	
	public UndoRedoDebugDialog(UndoHandler undoHandler) {
		setTitle("Undo / Redo Testdialog");
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		list = new JList(new UndoListModel(undoHandler));
		list.setCellRenderer(new UndoRedoListCellRenderer());
		
		add(new JScrollPane(list));
		
		pack();
		
		setLocationRelativeTo(null);

		new WindowPositionSaver(this);
	}
	
	
	
}
