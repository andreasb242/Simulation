package ch.zhaw.simulation.plugin.matlab.dirwatcher;

import java.io.File;

/**
 * @author: bachi
 */
public abstract class FileListener implements ResourceListener<File> {
	@Override
	public abstract void resourceAdded(File event);

	@Override
	public abstract void resourceChanged(File event);

	@Override
	public abstract void resourceDeleted(File event);
}
