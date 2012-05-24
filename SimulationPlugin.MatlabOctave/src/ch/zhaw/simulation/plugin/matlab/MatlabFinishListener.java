package ch.zhaw.simulation.plugin.matlab;

import java.io.File;

import ch.zhaw.simulation.plugin.ExecutionListener.FinishState;
import ch.zhaw.simulation.plugin.matlab.dirwatcher.FileListener;


/**
 * @author: bachi
 */
public class MatlabFinishListener extends FileListener {

	private MatlabCompatiblePlugin parent;

	private String workpath;

	public MatlabFinishListener(MatlabCompatiblePlugin parent) {
		this.parent = parent;
	}

	@Override
	public void resourceAdded(File event) {
		stop(event);
	}

	@Override
	public void resourceChanged(File event) {
		stop(event);
	}

	@Override
	public void resourceDeleted(File event) {
		//
	}

	private void stop(File event) {
		File file;

		synchronized (workpath) {
			file = new File(workpath + File.separator + "matlab_finish.txt");
		}

		if (event.equals(file)) {
			parent.watcher.stop();
			parent.provider.getExecutionListener().executionFinished(null, FinishState.SUCCESSFULLY);
		}
	}

	public void updateWorkpath(String workpath) {
		synchronized (workpath) {
			this.workpath = workpath;
		}
	}
}
