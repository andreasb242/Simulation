package ch.zhaw.simulation.gui.configuration.codeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import ch.zhaw.simulation.math.Constant;
import ch.zhaw.simulation.math.Function;
import ch.zhaw.simulation.model.element.NamedSimulationObject;
import ch.zhaw.simulation.model.element.SimulationGlobal;


/**
 * Thanks: http://groups.google.com/group/de.comp.lang.java/msg/2bbeb016abad270
 * 
 * IMPORTANT NOTE: regex should contain 1 group.
 * 
 * Using PlainView here because we don't want line wrapping to occur.
 */
public class FormulaView extends PlainView {

	private HashMap<Pattern, FormatEntry> patternColors;
	private static final String TAG_COMMENT = "(//.*(\n|$))";
	
	private static final FormatEntry ENTRY_DEFAULT = new FormatEntry(Color.BLACK, new Font("Monospaced", Font.PLAIN, 12));
	private static final FormatEntry ENTRY_COMMENT = new FormatEntry(new Color(0x3f7f5f), ENTRY_DEFAULT.font);
	private static final FormatEntry ENTRY_CONST = new FormatEntry(Color.BLUE, ENTRY_DEFAULT.font.deriveFont(Font.BOLD));
	private static final FormatEntry ENTRY_FUNCTION = new FormatEntry(new Color(0x891966), ENTRY_DEFAULT.font.deriveFont(Font.BOLD));
	private static final FormatEntry ENTRY_PARAMETER = new FormatEntry(new Color(0x50b3ff), ENTRY_DEFAULT.font.deriveFont(Font.BOLD));
	private static final FormatEntry ENTRY_GLOBAL = new FormatEntry(new Color(0x3a80b6), ENTRY_DEFAULT.font.deriveFont(Font.BOLD));

	public FormulaView(Element element) {
		super(element);

		// Set tabsize to 4 (instead of the default 8)
		getDocument().putProperty(PlainDocument.tabSizeAttribute, 3);

		// NOTE: the order is important!
		patternColors = new HashMap<Pattern, FormatEntry>();
	}

	@Override
	protected int drawUnselectedText(Graphics graphics, int x, int y, int p0,
			int p1) throws BadLocationException {

		Document doc = getDocument();
		String text = doc.getText(p0, p1 - p0);

		Segment segment = getLineBuffer();

		SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
		SortedMap<Integer, FormatEntry> colorMap = new TreeMap<Integer, FormatEntry>();

		// Match all regexes on this snippet, store positions
		for (Map.Entry<Pattern, FormatEntry> entry : patternColors.entrySet()) {

			Matcher matcher = entry.getKey().matcher(text);

			while (matcher.find()) {
				FormatEntry v = entry.getValue();
				int start = matcher.start(1);
				if(start != 0) {
					start += v.startPadding;
				}
				startMap.put(start, matcher.end() - v.endPadding);
				colorMap.put(start, v);
			}
		}

		int i = 0;

		// Colour the parts
		for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
			int start = entry.getKey();
			int end = entry.getValue();

			if (i < start) {
				graphics.setColor(ENTRY_DEFAULT.color);
				graphics.setFont(ENTRY_DEFAULT.font);
				doc.getText(p0 + i, start - i, segment);
				x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
			}

			FormatEntry format = colorMap.get(start);
			graphics.setColor(format.color);
			graphics.setFont(format.font);

			i = end;
			doc.getText(p0 + start, i - start, segment);
			x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
		}

		// Paint possible remaining text black
		if (i < text.length()) {
			graphics.setColor(ENTRY_DEFAULT.color);
			graphics.setFont(ENTRY_DEFAULT.font);
			doc.getText(p0 + i, text.length() - i, segment);
			x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
		}

		return x;
	}

	public void setHilighter(Constant[] constants, Function[] functions, Vector<NamedSimulationObject> parameter, Vector<SimulationGlobal> globals) {
		patternColors.clear();

		for (Constant c : constants) {
			String pattern = "(\\W|^)(" + c.name + ")\\W";
			patternColors.put(Pattern.compile(pattern), ENTRY_CONST.get(1, 1));
			pattern = "(\\W|^)(" + c.name + ")$";
			patternColors.put(Pattern.compile(pattern), ENTRY_CONST.get(1, 0));
		}

		for (Function f : functions) {
			String pattern = "(\\W|^)(" + f.getName() + ")\\(";
			patternColors.put(Pattern.compile(pattern), ENTRY_FUNCTION.get(1, 1));
		}

		for (NamedSimulationObject p : parameter) {
			String pattern = "(\\W|^)(" + p.getName() + ")\\W";
			patternColors.put(Pattern.compile(pattern), ENTRY_PARAMETER.get(1, 1));
			pattern = "(\\W|^)(" + p.getName() + ")$";
			patternColors.put(Pattern.compile(pattern), ENTRY_PARAMETER.get(1, 0));
		}
		
		for(SimulationGlobal g : globals) {
			String pattern = "(\\W|^)(" + g.getName() + ")\\W";
			patternColors.put(Pattern.compile(pattern), ENTRY_GLOBAL.get(1, 1));
			pattern = "(\\W|^)(" + g.getName() + ")$";
			patternColors.put(Pattern.compile(pattern), ENTRY_GLOBAL.get(1, 0));
		}

		patternColors.put(Pattern.compile(TAG_COMMENT), ENTRY_COMMENT);
	}

	static class FormatEntry {
		public final Color color;
		public final Font font;
		public final int startPadding;
		public final int endPadding;
		
		public FormatEntry(Color color, Font font) {
			this.color = color;
			this.font = font;
			this.startPadding = 0;
			this.endPadding = 0;
		}
		
		public FormatEntry(Color color, Font font, int sp, int ep) {
			this.color = color;
			this.font = font;
			this.startPadding = sp;
			this.endPadding = ep;
		}
		
		public FormatEntry get(int sp, int ep) {
			return new FormatEntry(color, font, sp, ep);
		}
	}
}
