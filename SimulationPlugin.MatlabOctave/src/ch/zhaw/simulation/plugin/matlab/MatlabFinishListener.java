package ch.zhaw.simulation.plugin.matlab;

import butti.javalibs.dirwatcher.DirectoryWatcher;
import butti.javalibs.dirwatcher.FileListener;
import ch.zhaw.simulation.plugin.PluginDataProvider;

import java.io.File;

/**
 * @author: bachi
 */
public class MatlabFinishListener extends FileListener {

	private PluginDataProvider dataProvider;
	private DirectoryWatcher watcher;

	private String workpath;

	public MatlabFinishListener(PluginDataProvider dataProvider, DirectoryWatcher watcher) {
		this.dataProvider = dataProvider;
		this.watcher = watcher;
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
			file = new File(workpath + File.separator + "matlab_finish");
		}

		if (event.equals(file)) {
			watcher.stop();
			dataProvider.getExecutionListener().executionFinished();
		}
	}

	public void updateWorkpath(String workpath) {
		synchronized (workpath) {
			this.workpath = workpath;
		}
	}
}
