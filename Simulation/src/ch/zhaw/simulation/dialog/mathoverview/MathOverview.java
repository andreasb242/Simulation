package ch.zhaw.simulation.dialog.mathoverview;

import java.awt.Window;
import java.util.Vector;

import butti.javalibs.gui.BDialog;
import ch.zhaw.simulation.gui.codeditor.library.FormulaLibraryPanel;
import ch.zhaw.simulation.math.Parser;
import ch.zhaw.simulation.model.xy.DensityData;

public class MathOverview extends BDialog {
	private static final long serialVersionUID = 1L;

	public MathOverview(Window parent) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Matheüberblick");

		Parser parser = new Parser(new Vector<DensityData>());
		FormulaLibraryPanel library = new FormulaLibraryPanel();
		library.setParser(parser);

		add(library);

		setSize(640, 480);
		setLocationRelativeTo(parent);
	}

	public static void main(String[] args) {
		MathOverview m = new MathOverview(null);
		m.setVisible(true);
	}
}
