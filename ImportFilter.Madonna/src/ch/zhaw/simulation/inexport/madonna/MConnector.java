package ch.zhaw.simulation.inexport.madonna;

import java.awt.Point;

/**
 * An Connector
 * 
 * @author Andreas Butti
 */
public class MConnector extends MadonnaElement {
	/**
	 * The from / to fileintern IDs
	 */
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
