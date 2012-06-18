package ch.zhaw.simulation.dialog.mathoverview;

import java.awt.Window;

import javax.swing.JScrollPane;

import butti.javalibs.gui.BDialog;
import ch.zhaw.simulation.gui.codeditor.library.FormulaLibraryPanel;
import ch.zhaw.simulation.math.Parser;

public class MathOverview extends BDialog {
	private static final long serialVersionUID = 1L;

	public MathOverview(Window parent) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Matheüberblick");

		FormulaLibraryPanel library = new FormulaLibraryPanel(new Parser());

		add(new JScrollPane(library));

		setSize(640, 480);
		setLocationRelativeTo(parent);
	}

	public static void main(String[] args) {
		MathOverview m = new MathOverview(null);
		m.setVisible(true);
	}
}
