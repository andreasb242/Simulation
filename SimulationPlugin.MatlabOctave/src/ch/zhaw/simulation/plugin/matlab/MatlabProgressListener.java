package ch.zhaw.simulation.plugin.matlab;

import butti.javalibs.dirwatcher.FileListener;

import java.io.File;

public class MatlabProgressListener extends FileListener {

	private static final String PREFIX = "progress_";

	private MatlabCompatiblePlugin parent;

	public MatlabProgressListener(MatlabCompatiblePlugin parent) {
		this.parent = parent;
	}

	@Override
	public void resourceAdded(File event) {
		updateState(event);
	}

	@Override
	public void resourceChanged(File event) {
		updateState(event);
	}

	@Override
	public void resourceDeleted(File event) {
		//
	}

	private void updateState(File event) {
		int state;
		if (event.getName().startsWith(PREFIX)) {
			state = Integer.valueOf(event.getName().replaceAll(PREFIX, ""));
			parent.provider.getExecutionListener().setState(state);
		}
	}
}
