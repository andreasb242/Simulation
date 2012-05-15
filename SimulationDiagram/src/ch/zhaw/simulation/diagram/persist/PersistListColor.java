package ch.zhaw.simulation.diagram.persist;

import java.awt.Color;

import ch.zhaw.simulation.diagram.HtmlColorHelper;

public class PersistListColor extends AbstractPersistList<Color> {
	public PersistListColor(String id) {
		super(id);
	}

	@Override
	protected String elementToString(Color e) {
		return HtmlColorHelper.colorAsHexString(e);
	}

	@Override
	protected Color elementFromString(String data) {
		if (data == null) {
			return Color.BLACK;
		}

		return HtmlColorHelper.colorFromHexString(data);
	}

}
