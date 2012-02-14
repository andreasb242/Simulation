package ch.zhaw.simulation.inexport.madonna;

import java.awt.Point;

public class MConnector extends MadonnaElement {

	private int fromId;
	private int toId;

	public MConnector(int fromId, int toId, Point point) {
		super(point);
		this.fromId = fromId;
		this.toId = toId;
	}

	public int getFromId() {
		return fromId;
	}

	public int getToId() {
		return toId;
	}
}
