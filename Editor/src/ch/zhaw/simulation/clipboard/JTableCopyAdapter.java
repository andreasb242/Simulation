package ch.zhaw.simulation.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

/**
 * ExcelAdapter enables Copy Clipboard functionality on JTables. The clipboard
 * data format used by the adapter is compatible with the clipboard format used
 * by Excel. This provides for clipboard interoperability between enabled
 * JTables and Excel.
 * 
 * TODO currently unused
 */
public class JTableCopyAdapter implements ActionListener {
	private Clipboard system;
	private StringSelection stsel;
	private JTable table;

	/**
	 * The Excel Adapter is constructed with a JTable on which it enables
	 * Copy-Paste and acts as a Clipboard listener.
	 */
	public JTableCopyAdapter(JTable table) {
		this.table = table;
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.
		table.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * This method is activated on the Keystrokes we are listening to in this
	 * implementation. Here it listens for Copy and Paste ActionCommands.
	 * Selections comprising non-adjacent cells result in invalid selection and
	 * then copy action cannot be performed. Paste is done by aligning the upper
	 * left corner of the selection with the 1st element in the current
	 * selection of the JTable.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Copy") == 0) {
			copy();
		}
	}

	public void copy() {
		StringBuffer sbf = new StringBuffer();
		// Check to ensure we have selected only a contiguous block of cells
		int numcols = table.getSelectedColumnCount();
		int numrows = table.getSelectedRowCount();
		int[] rowsselected = table.getSelectedRows();
		int[] colsselected = table.getSelectedColumns();

		if (rowsselected.length < 1 || colsselected.length < 1) {
			return;
		}

		if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] && numrows == rowsselected.length) && (numcols - 1 == colsselected[colsselected.length - 1]
				- colsselected[0] && numcols == colsselected.length))) {
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// copy header

		for (int j = 0; j < numcols; j++) {
			sbf.append(table.getColumnName(colsselected[j]));
			if (j < numcols - 1) {
				sbf.append("\t");
			}
		}
		sbf.append("\n");

		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {
				sbf.append(table.getValueAt(rowsselected[i], colsselected[j]));
				if (j < numcols - 1) {
					sbf.append("\t");
				}
			}
			sbf.append("\n");
		}
		stsel = new StringSelection(sbf.toString());
		system.setContents(stsel, stsel);
	}
}
