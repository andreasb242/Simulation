package ch.zhaw.simulation.diagram;

import java.awt.Color;

public class HtmlColorHelper {

	public static String getColorHex(Color color) {
		final String ZEROES = "000000";
		String s = Integer.toString(color.getRGB() & 0x00ffffff, 16);
		return s.length() <= 6 ? ZEROES.substring(s.length()) + s : s;
	}
}
