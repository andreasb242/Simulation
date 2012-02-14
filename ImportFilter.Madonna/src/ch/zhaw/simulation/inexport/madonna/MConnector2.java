package ch.zhaw.simulation.inexport.madonna;

public class MConnector2 extends MadonnaElement {

	private int fromId;
	private int toId;

	public MConnector2(int fromId, int toId) {
		super(null);

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
