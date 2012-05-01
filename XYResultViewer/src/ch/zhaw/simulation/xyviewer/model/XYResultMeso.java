package ch.zhaw.simulation.xyviewer.model;

public class XYResultMeso {
	private int colorId;
	private int x;
	private int y;

	public XYResultMeso(int colorId, int x, int y) {
		this.colorId = colorId;
		this.x = x;
		this.y = y;
	}

	public int getColorId() {
		return colorId;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
