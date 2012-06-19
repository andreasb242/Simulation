package ch.zhaw.simulation.inexport.madonna.binformat;


/**
 * A flow element
 * 
 * @author Andreas Butti
 */
public class MFlow extends MadonnaElement {
	/**
	 * The from / to fileintern IDs
	 */
	private int fromId;
	private int toId;

	public MFlow(int fromId, int toId) {
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
