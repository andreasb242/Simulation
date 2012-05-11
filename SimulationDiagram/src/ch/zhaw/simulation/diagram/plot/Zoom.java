package ch.zhaw.simulation.diagram.plot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class Zoom {
	private int yOffset = 100;
	private int xOffset = 0;
	private int xZoom = 100;
	private int yZoom = 100;

	private Vector<ActionListener> listeners = new Vector<ActionListener>();

	public Zoom() {
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

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
		fireActionPerformed(new ActionEvent(this, yOffset, "yOffset"));
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
		fireActionPerformed(new ActionEvent(this, xOffset, "xOffset"));
	}

	public void setxZoom(int xZoom) {
		this.xZoom = xZoom;
		fireActionPerformed(new ActionEvent(this, xZoom, "xZoom"));
	}

	public void setyZoom(int yZoom) {
		this.yZoom = yZoom;
		fireActionPerformed(new ActionEvent(this, yZoom, "yZoom"));
	}

	public int getyOffset() {
		return yOffset;
	}

	public int getxOffset() {
		return xOffset;
	}

	public int getxZoom() {
		return xZoom;
	}

	public int getyZoom() {
		return yZoom;
	}

}
