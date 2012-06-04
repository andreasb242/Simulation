package ch.zhaw.simulation.xyviewer;

import java.util.Vector;

import javax.swing.SwingUtilities;

public class PositionModel {
	private int stepCount;
	private Vector<PositionListener> listener = new Vector<PositionListener>();

	public PositionModel(int stepCount) {
		this.stepCount = stepCount;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void firePosition(final int pos) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					firePosition(pos);
				}
			});

			for (PositionListener l : listener) {
				l.positionChanged(pos);
			}
		}
	}

	public void addListener(PositionListener l) {
		this.listener.add(l);
	}

	public void removeListener(PositionListener l) {
		this.listener.remove(l);
	}
}
