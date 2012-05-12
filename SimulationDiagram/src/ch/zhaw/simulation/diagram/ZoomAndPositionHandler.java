package ch.zhaw.simulation.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ZoomAndPositionHandler {
	private int offsetY = 100;
	private int offsetX = 0;
	private double zoomX = 100;
	private double zoomY = 100;

	/**
	 * The padding between the border and the graph
	 * 
	 * TODO Configurable
	 */
	private int borderPadding = 30;

	/**
	 * The offset for SeriePlot, so the lowest value in the Y-Axis is zero
	 */
	private double dataOffsetY = 0;

	private Vector<ActionListener> listeners = new Vector<ActionListener>();

	public ZoomAndPositionHandler() {
	}

	public void addListener(ActionListener l) {
		listeners.add(l);
	}

	public void removeListener(ActionListener l) {
		listeners.remove(l);
	}

	private void fireActionPerformed(ActionEvent a) {
		for (ActionListener l : this.listeners) {
			l.actionPerformed(a);
		}
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
		fireActionPerformed(new ActionEvent(this, 0, "offsetY"));
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
		fireActionPerformed(new ActionEvent(this, 0, "offsetX"));
	}

	public double getZoomX() {
		return zoomX;
	}

	public void setZoomX(double zoomX) {
		this.zoomX = zoomX;
		fireActionPerformed(new ActionEvent(this, 0, "zoomX"));
	}

	public double getZoomY() {
		return zoomY;
	}

	public void setZoomY(double zoomY) {
		this.zoomY = zoomY;
		fireActionPerformed(new ActionEvent(this, 0, "zoomY"));
	}

	public double getDataOffsetY() {
		return dataOffsetY;
	}

	public void setDataOffsetY(double dataOffsetY) {
		this.dataOffsetY = dataOffsetY;
		fireActionPerformed(new ActionEvent(this, 0, "dataOffsetY"));
	}

	public int getBorderPadding() {
		return borderPadding;
	}

}
