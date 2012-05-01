package ch.zhaw.simulation.model.element;

import java.util.Vector;

/**
 * Superclass for subclasses like
 * o Infinite (InfiniteData)
 * o Container (SimulationContainerData)
 * o Parameter (SimulationParameterData)
 * o Meso (MesoData)
 * o Text (TextData)
 * o Density (SimulationDensityContainerData)
 * o Global (SimulationGlobalData)
 * o FlowValve (FlowValveData)
 *
 * @author Andreas Butti
 */
public abstract class AbstractSimulationData {
	/**
	 * The X coordinate on the document, on top left
	 */
	private int x;

	/**
	 * The Y coordinate on the document, on top left
	 */
	private int y;

	/**
	 * A unique Id of this item
	 * (used for copy & paste)
	 */
	private int id;

	/**
	 * Static next ID, used to create the next unique ID
	 */
	private static int sid = 1;

	/**
	 * If this object references globals, this are saved here during parsing and
	 * checking formula
	 */
	private Vector<SimulationGlobalData> usedGlobals = null;

	/**
	 * Creates a Simulation object
	 * 
	 * @param x
	 *            The X coordinate on the Document
	 * @param y
	 *            The Y coordinate on the Document
	 */
	public AbstractSimulationData(int x, int y) {
		this.x = x;
		this.y = y;
		id = sid++;
	}

	/**
	 * @return The unique ID of this element
	 */
	public int getId() {
		return id;
	}

	/**
	 * The supplied coordinates are the center of the object instead of the top
	 * left, so we correct this here with this call
	 */
	public void calcCenter() {
		x -= getWidth() / 2;
		y -= getHeight() / 2;
	}

	/**
	 * @return The X coordinate, from left
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return The X coordinate, from right
	 */
	public int getX2() {
		return x + getWidth();
	}

	/**
	 * @return The X coordinate, from center
	 */
	public int getXCenter() {
		return x + getWidth() / 2;
	}

	/**
	 * Sets the X coordinate from left
	 * 
	 * @param x
	 *            The new coordinate
	 */
	public void setX(int x) {
		this.x = x;
		if (this.x < 0) {
			this.x = 0;
		}
	}

	/**
	 * @return The Y coordinate, top
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return The Y coordinate, bottom
	 */
	public int getY2() {
		return y + getHeight();
	}

	/**
	 * @return The Y coordinate, center
	 */
	public int getYCenter() {
		return y + getHeight() / 2;
	}

	/**
	 * Sets the Y coordinate from top
	 * 
	 * @param y
	 *            The new coordinate
	 */
	public void setY(int y) {
		this.y = y;
		if (this.y < 0) {
			this.y = 0;
		}
	}

	/**
	 * Returns true if this object intersects another object
	 * 
	 * @param other
	 *            The other object
	 * @return true if intersects
	 */
	public boolean intersects(AbstractSimulationData other) {
		int tw = getWidth();
		int th = getHeight();
		int rw = other.getWidth();
		int rh = other.getHeight();
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = this.x;
		int ty = this.y;
		int rx = other.x;
		int ry = other.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		// overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
	}

	/**
	 * Move this object
	 * 
	 * @param dX
	 *            Delta X
	 * @param dY
	 *            Delta Y
	 */
	public void move(int dX, int dY) {
		setX(this.x + dX);
		setY(this.y + dY);
	}

	/**
	 * @return the width of the Object, may static (e.g. Container) or dynamic
	 *         (e.g. Comment)
	 */
	public abstract int getWidth();

	/**
	 * @return the height of the Object, may static (e.g. Container) or dynamic
	 *         (e.g. Comment)
	 */
	public abstract int getHeight();

	/**
	 * If this object references globals, this are saved here during parsing and
	 * checking formula
	 * 
	 * @param usedGlobals
	 *            The vector to save
	 */
	public void setUsedGlobals(Vector<SimulationGlobalData> usedGlobals) {
		this.usedGlobals = usedGlobals;
	}

	/**
	 * @return referenced globals if any set
	 */
	public Vector<SimulationGlobalData> getUsedGlobals() {
		return usedGlobals;
	}
}
