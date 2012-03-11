package ch.zhaw.simulation.gui.codeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.util.DrawHelper;
import ch.zhaw.simulation.gui.codeditor.Autocomplete.AutocompleteWord;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.model.element.AbstractNamedSimulationData;
import ch.zhaw.simulation.model.element.SimulationGlobalData;

public class FormulaTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;

	private FormulaEditorKit editor = new FormulaEditorKit();

	public UnderlineHighlightPainter highlighter = new UnderlineHighlightPainter(new Color(0xff5555));
	private Object lastErrorHighlighter = null;

	private Autocomplete autocomplete;

	private FunctionHelp help;

	public FormulaTextPane(FunctionHelp help) {
		setEditorKitForContentType("text/formula", editor);
		setContentType("text/formula");
		setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.help = help;

		System.out.println(this.help);

		new LineHighlighter(this, new Color(0xfffbbd));

		setBackground(Color.WHITE);

		autocomplete = new Autocomplete(this);
	}

	public void setError(int line, int width) {
		try {
			if (lastErrorHighlighter != null) {
				getHighlighter().removeHighlight(lastErrorHighlighter);
				lastErrorHighlighter = null;
			}

			if (line == 0 && width == 0) {
				return;
			}

			int start = getLinePos(line);

			lastErrorHighlighter = getHighlighter().addHighlight(start, start + width, highlighter);
		} catch (BadLocationException e) {
			Errorhandler.logError(e);
		}
	}

	private int getLinePos(int line) {
		String txt = getText();

		for (int i = 0; i < txt.length(); i++) {
			if (txt.charAt(i) == '\n') {
				line--;
				if (line == 0) {
					return i + 1;
				}
			}
		}

		return 0;
	}

	public void inserEditor(String text, int relCursor) {
		int p = getCaretPosition();
		try {
			getDocument().insertString(p, text, null);
			p += text.length() + relCursor;
			setCaretPosition(p);
		} catch (BadLocationException e) {
			Errorhandler.logError(e);
		}
	}

	@Override
	public void paint(Graphics g) {
		DrawHelper.antialisingOn(g);
		super.paint(g);
	}

	public void setConsts(Constant[] constants, Function[] functions, Vector<AbstractNamedSimulationData> parameter, Vector<SimulationGlobalData> globals) {
		editor.setConsts(constants, functions, parameter, globals);
	}

	public void clearAutocompletet() {
		autocomplete.clearAutocomplete();
	}

	public void addAutocomplete(AutocompleteWord word) {
		autocomplete.addAutocomplete(word);
	}
}
