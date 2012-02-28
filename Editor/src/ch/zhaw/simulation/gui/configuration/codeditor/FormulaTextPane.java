package ch.zhaw.simulation.gui.configuration.codeditor;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;

import ch.zhaw.simulation.gui.configuration.codeditor.Autocomplete.AutocompleteWord;
import ch.zhaw.simulation.help.model.FunctionHelp;
import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;

import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.util.DrawHelper;

public class FormulaTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;

	private FormulaEditorKit editor = new FormulaEditorKit();

	public UnderlineHighlightPainter highlighter = new UnderlineHighlightPainter(
			new Color(0xff5555));
	private Object lastErrorHighlighter = null;

	private CodeTooltip tip;
	
	private Autocomplete autocomplete;

	private FunctionHelp help;

	public FormulaTextPane(FunctionHelp help) {
		setEditorKitForContentType("text/formula", editor);
		setContentType("text/formula");
		setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.help = help;
		
		// TODO debug
		System.out.println(this.help);

		new LineHighlighter(this, new Color(0xfffbbd));

		setBackground(Color.WHITE);
		
		tip = new CodeTooltip();
		initTooltip(0);
		
		autocomplete = new Autocomplete(this);
		setToolTipText(" ");
	}
	
	private void initTooltip(int pos) {
		if(getText().length() == 0) {
			return;
		}
		int start = findWordStart(pos);
		int end = findWordEnd(pos);
		
		if(end < start) {
			return;
		}
		System.out.println("start: " + start + "; end: " + end);
		
		System.out.println(getText().substring(start, end));
		
		// TODO: Tooltip implementieren
		tip.setTitle("Funktion xyz(x, y, z)");
		tip.setContents("dies ist ein Test" + pos);

		tip.setTitle("Kontextabhängige Hilfe");
		tip.setContents("<html>Fahren Sie mit der Maus über<br>eine Funktion oder Variable</html>");
	}
	
	private int findWordStart(int pos) {
		String text = getText();
		int i;
		
		if(pos >= text.length()) {
			return text.length() - 1;
		}
		
		for(i = pos; i > 0; i--) {
			System.out.println(i);
			char c = text.charAt(i);
			if(c == ')' || c == ' ' || c == '+' || c == '-' || c == '+' || c == '/' || c == '*' || c == '^' || c == '&' || c == '|' || c == '%' || c == '!') {
				return i + 1;
			}
		}
		
		return i;
	}

	private int findWordEnd(int pos) {
		String text = getText();
		for(int i = pos; i < text.length(); i++) {
			char c = text.charAt(i);
			if(c == '(' || c == ' ' || c == '+' || c == '-' || c == '+' || c == '/' || c == '*' || c == '^' || c == '&' || c == '|' || c == '%' || c == '!') {
				return i;
			}
		}
		return pos;
	}

	@Override
	public JToolTip createToolTip() {
		return tip;
	}
	
	public Point getToolTipLocation(MouseEvent event) {
		int pos = getUI().viewToModel(this, event.getPoint());
		initTooltip(pos);
		Point p = event.getPoint();
		return new Point(p.x, p.y + 15);
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

			lastErrorHighlighter = getHighlighter().addHighlight(start,
					start + width, highlighter);
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

	public void setConsts(Constant[] constants, Function[] functions,
			Vector<NamedSimulationObject> parameter, Vector<SimulationGlobal> globals) {
		editor.setConsts(constants, functions, parameter, globals);
	}

	public void clearAutocompletet() {
		autocomplete.clearAutocomplete();
	}

	public void addAutocomplete(AutocompleteWord word) {
		autocomplete.addAutocomplete(word);
	}
}
