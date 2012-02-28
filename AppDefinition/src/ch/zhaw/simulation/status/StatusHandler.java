package ch.zhaw.simulation.status;

import java.util.Vector;

public class StatusHandler implements StatusListener {
	private Vector<StatusListener> listener = new Vector<StatusListener>();

	public StatusHandler() {
	}

	public void addListener(StatusListener l) {
		listener.add(l);
	}

	public void removeListener(StatusListener l) {
		listener.remove(l);
	}

	@Override
	public void clearStatus() {
		for (StatusListener l : listener) {
			l.clearStatus();
		}
	}

	@Override
	public void setStatusText(String text) {
		for (StatusListener l : listener) {
			l.setStatusText(text);
		}
	}

	@Override
	public void setStatusTextInfo(String text) {
		for (StatusListener l : listener) {
			l.setStatusTextInfo(text);
		}
	}

}
