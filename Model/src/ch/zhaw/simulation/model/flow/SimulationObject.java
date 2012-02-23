package ch.zhaw.simulation.model.flow;

import java.util.Vector;

public abstract class SimulationObject {
	private int x;
	private int y;
	private int id;

	private static int sid = 1;

	private Vector<SimulationGlobal> usedGlobals = null;

	public SimulationObject(int x, int y) {
		this.x = x;
		this.y = y;
		id = sid++;
	}

	public int getId() {
		return id;
	}

	public void calcCenter() {
		x -= getWidth() / 2;
		y -= getHeight() / 2;
	}

	public int getX() {
		return x;
	}

	public int getX2() {
		return x + getWidth();
	}

	public int getXCenter() {
		return x + getWidth() / 2;
	}

	public void setX(int x) {
		this.x = x;
		if (this.x < 0) {
			this.x = 0;
		}
	}

	public int getY() {
		return y;
	}

	public int getY2() {
		return y + getHeight();
	}

	public int getYCenter() {
		return y + getHeight() / 2;
	}

	public void setY(int y) {
		this.y = y;
		if (this.y < 0) {
			this.y = 0;
		}
	}

	public boolean intersects(SimulationObject other) {
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

	public void move(int dX, int dY) {
		setX(this.x + dX);
		setY(this.y + dY);
	}

	public abstract int getWidth();

	public abstract int getHeight();

	public void setUsedGlobals(Vector<SimulationGlobal> usedGlobals) {
		this.usedGlobals = usedGlobals;
	}

	public Vector<SimulationGlobal> getUsedGlobals() {
		return usedGlobals;
	}
}
