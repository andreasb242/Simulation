package ch.zhaw.simulation.diagram;

import java.awt.Color;

public class HexColorHelper {

	public static String colorAsHexString(Color color) {
		if (color == null) {
			color = Color.BLACK;
		}

		final String ZEROES = "000000";
		String s = Integer.toString(color.getRGB() & 0x00ffffff, 16);
		return s.length() <= 6 ? ZEROES.substring(s.length()) + s : s;
	}

	public static Color colorFromHexString(String str) {
		if (str == null) {
			return null;
		}

		try {
			int c = Integer.parseInt(str, 16);
			return new Color(c);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
