package ch.zhaw.simulation.dialog.snapshot;

import java.io.File;

public interface MovieExportable {

	/**
	 * Called from Thread != Event Dispatcher Thread
	 */
	public int getCount();

	/**
	 * Called from Thread != Event Dispatcher Thread
	 */
	public void export(int pos, File file) throws Exception;

	/**
	 * Called from Thread != Event Dispatcher Thread
	 */
	public int getFps();

}
