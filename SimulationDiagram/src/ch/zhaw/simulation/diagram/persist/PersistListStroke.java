package ch.zhaw.simulation.diagram.persist;

import java.awt.BasicStroke;
import java.util.StringTokenizer;
import java.util.Vector;

import ch.zhaw.simulation.diagram.DiagramStrokeFactory;

public class PersistListStroke extends AbstractPersistList<BasicStroke> {
	public PersistListStroke(String id) {
		super(id);
	}

	@Override
	protected BasicStroke elementFromString(String data) {
		if (data == null) {
			return DiagramStrokeFactory.createStroke();
		}

		StringTokenizer token = new StringTokenizer(data, ",");

		float width = 2;

		if (token.hasMoreTokens()) {
			try {
				width = Float.parseFloat(token.nextToken());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Vector<Float> vDash = new Vector<Float>();

		while (token.hasMoreTokens()) {
			String v = token.nextToken();

			try {
				float d = Float.parseFloat(v);
				vDash.add(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		float[] dash = null;
		if (vDash.size() > 0) {
			dash = new float[vDash.size()];
			for (int i = 0; i < dash.length; i++) {
				dash[i] = vDash.get(i);
			}
		}

		return DiagramStrokeFactory.createStroke(width, dash);
	}

	@Override
	protected String elementToString(BasicStroke e) {
		if (e == null) {
			return "";
		}

		StringBuilder b = new StringBuilder();
		b.append(e.getLineWidth());

		float[] dash = e.getDashArray();
		if (dash != null) {
			for (float d : dash) {
				b.append(",");
				b.append(d);
			}
		}

		return b.toString();
	}

}
