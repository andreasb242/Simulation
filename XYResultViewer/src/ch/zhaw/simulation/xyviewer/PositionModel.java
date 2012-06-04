package ch.zhaw.simulation.xyviewer;

import java.util.Vector;

import javax.swing.SwingUtilities;

public class PositionModel {
	private int stepCount;
	private int pos = 0;

	private Vector<PositionListener> listener = new Vector<PositionListener>();

	public PositionModel(int stepCount) {
		this.stepCount = stepCount;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void firePosition(final int pos) {
		this.pos = pos;

		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					firePosition(pos);
				}
			});
		} else {
			for (PositionListener l : listener) {
				l.positionChanged(pos);
			}
		}
	}

	public boolean positionPrev() {
		int pos = this.pos + -1;
		if (pos < 0) {
			firePosition(0);
			return false;
		}
		firePosition(pos);
		return true;
	}

	public boolean positionNext() {
		int pos = this.pos + 1;
		if (pos > this.stepCount) {
			pos = this.stepCount;
			firePosition(pos);
			return false;
		}

		firePosition(pos);
		return true;
	}

	public boolean isLast() {
		return pos >= this.stepCount;
	}

	public void positionEnd() {
		firePosition(this.stepCount);
	}

	public void positionStart() {
		firePosition(0);
	}

	public void addListener(PositionListener l) {
		this.listener.add(l);
	}

	public void removeListener(PositionListener l) {
		this.listener.remove(l);
	}
}
