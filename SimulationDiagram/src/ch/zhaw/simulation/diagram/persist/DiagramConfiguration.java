package ch.zhaw.simulation.diagram.persist;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import butti.javalibs.util.StringUtil;
import ch.zhaw.simulation.diagram.HexColorHelper;

public class DiagramConfiguration {
	private Properties data = new Properties();
	private boolean changed = false;

	public void set(String key, String value) {
		if (StringUtil.equals(data.getProperty(key), value)) {
			return;
		}

		if (value == null) {
			value = "";
		}

		data.put(key, value);
		changed = true;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isChanged() {
		return changed;
	}

	public String get(String key) {
		return data.getProperty(key);
	}

	public String get(String key, String defaultValue) {
		String value = get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public void store(OutputStream out) throws IOException {
		data.store(out, "(AB)² Simulation Diagram Config");
	}

	public void load(InputStream in) throws IOException {
		data.load(in);
	}

	public void set(String key, boolean value) {
		set(key, Boolean.toString(value));
	}

	public boolean get(String key, boolean defaultValue) {
		String data = get(key);
		if (data == null) {
			return defaultValue;
		}

		return Boolean.parseBoolean(data);
	}

	public void set(String key, Font font) {
		if (font == null) {
			set(key, "");
			return;
		}

		StringBuilder b = new StringBuilder();

		b.append(font.getName());
		b.append(":");
		b.append(font.getStyle());
		b.append(":");
		b.append(font.getSize());

		set(key, b.toString());
	}

	public Font get(String key, Font defaultFont) {
		String data = get(key);
		if (data == null) {
			return defaultFont;
		}

		String[] parts = data.split(":");
		if (parts.length != 3) {
			return defaultFont;
		}

		try {
			return new Font(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void set(String key, Color color) {
		set(key, HexColorHelper.colorAsHexString(color));
	}

	public Paint get(String key, Color defaultColor) {
		String data = get(key);
		Color color = HexColorHelper.colorFromHexString(data);
		if (color == null) {
			return defaultColor;
		}
		return color;
	}

	public void set(String key, double value) {
		set(key, Double.toString(value));
	}

	public double get(String key, double defaultValue) {
		String data = get(key);
		if (data == null) {
			return defaultValue;
		}

		try {
			return Double.parseDouble(data);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public void set(String key, BasicStroke stroke) {
		if (stroke == null) {
			set(key, "");
			return;
		}

		StringBuilder b = new StringBuilder();

		b.append(stroke.getLineWidth());
		b.append(":");
		b.append(stroke.getEndCap());
		b.append(":");
		b.append(stroke.getLineJoin());
		b.append(":");
		b.append(stroke.getMiterLimit());
		b.append(":");
		b.append(stroke.getDashPhase());

		float[] dash = stroke.getDashArray();
		if (dash != null) {
			for (float d : dash) {
				b.append(":");
				b.append(d);
			}
		}

		set(key, b.toString());
	}

	public Stroke get(String key, BasicStroke defaultStroke) {
		String data = get(key);
		if (data == null) {
			return defaultStroke;
		}

		String[] parts = data.split(":");
		if (parts.length < 5) {
			return defaultStroke;
		}

		StringTokenizer token = new StringTokenizer(data, ":");

		Vector<Float> vDash = new Vector<Float>();

		while (token.hasMoreTokens()) {
			try {
				vDash.add(Float.parseFloat(token.nextToken()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		float[] dash = null;
		if (vDash.size() > 5) {
			dash = new float[vDash.size() - 5];
			for (int i = 0; i < dash.length - 5; i++) {
				dash[i] = vDash.get(i + 5);
			}
		}

		return new BasicStroke(vDash.get(0), (int) vDash.get(1).floatValue(), (int) vDash.get(2).floatValue(), vDash.get(3), dash, vDash.get(4));
	}
}
