package ch.zhaw.simulation.window.xy.sidebar.config.font;

import java.awt.Font;
import java.io.InputStream;

import butti.javalibs.errorhandler.Errorhandler;

/**
 * The fonts installed on Windows don't support x-Point (ẋ) and x-Point-Point
 * (ẍ) chars, so we load our custom font
 * 
 * @author Andreas Butti
 */
public class FontLoader {
	private static Font font;
	private static final int FONT_SIZE = 18;

	private FontLoader() {
	}

	public static Font getFont() {
		if (font != null) {
			return font;
		}

		try {
			InputStream is = FontLoader.class.getResourceAsStream("FreeSans.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			font = font.deriveFont((float)FONT_SIZE);
		} catch (Exception ex) {
			Errorhandler.showError(ex, "Schrift konnte nicht geladen werden, möglicherweise können die Ableitungszeichen nicht dargestellt werden!");

			font = new Font("sans", Font.PLAIN, FONT_SIZE);
		}

		return font;
	}

}
