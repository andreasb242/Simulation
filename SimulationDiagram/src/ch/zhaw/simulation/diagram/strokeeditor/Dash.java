package ch.zhaw.simulation.diagram.strokeeditor;

import java.util.Vector;

/**
 * An editable dash
 * 
 * @author Andreas Butti
 */
public class Dash {
	private float[] dash;

	public Dash(float[] dash) {
		this.dash = dash;
	}

	public Dash(String s) {
		parse(s);
	}

	public void parse(String s) {
		if (s == null || s.length() == 0) {
			this.dash = null;
			return;
		}

		Vector<Float> data = new Vector<Float>();
		for (String part : s.split(" ")) {
			part = part.trim();
			if("".equals(part)) {
				continue;
			}
			try {
				float f = Float.parseFloat(part);
				data.add(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (data.size() == 0) {
			this.dash = null;
		} else {
			this.dash = new float[data.size()];
			for (int i = 0; i < data.size(); i++) {
				this.dash[i] = data.get(i);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Dash) {
			Dash d = (Dash) obj;
			if (d.dash == this.dash) {
				return true;
			}

			if (d.dash == null || this.dash == null) {
				return false;
			}

			if (d.dash.length != d.dash.length) {
				return false;
			}

			for (int i = 0; i < this.dash.length; i++) {
				if (d.dash[i] != this.dash[i]) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	public float[] getDash() {
		return dash;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		if (this.dash != null) {
			for (float f : this.dash) {
				b.append(" ");
				b.append(f);
			}
		} else {
			return "";
		}
		return b.substring(1);
	}
}