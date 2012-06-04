package ch.zhaw.simulation.plugin.matlab.dirwatcher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: bachi
 */
public class DirectoryWatcher extends ResourceWatcher<FileListener, File> {
	private Map<String, Long> currentFiles;
	private Map<String, Long> previousFiles;
	private File directoryFile;

	public DirectoryWatcher(int interval) throws IllegalArgumentException {
		super(interval);
		currentFiles = new HashMap<String, Long>();
		previousFiles = new HashMap<String, Long>();
	}

	public void start(String directory) throws IllegalArgumentException {
		this.directoryFile = new File(directory);
		if (!this.directoryFile.isDirectory()) {
			throw new IllegalArgumentException("not a directory: " + directory);
		}
		updateFiles();
		super.start();
	}

	private void updateFiles() {
		previousFiles.clear();
		previousFiles.putAll(currentFiles);

		currentFiles.clear();

		File[] content = directoryFile.listFiles();
		if (content != null) {
			for (File file : content) {
				currentFiles.put(file.getAbsolutePath(), file.lastModified());
			}
		} else {
			System.out.println("DirectoryWatcher::Folder «" + directoryFile + "» deleted?!");
		}
	}

	@Override
	public void doInterval() {
		updateFiles();

		for (Map.Entry<String, Long> currentEntry : currentFiles.entrySet()) {
			if (!previousFiles.containsKey(currentEntry.getKey())) {
				resourceAdded(new File(currentEntry.getKey()));
			} else if (previousFiles.get(currentEntry.getKey()).compareTo(currentEntry.getValue()) != 0) {
				resourceChanged(new File(currentEntry.getKey()));
			}
		}

		for (Map.Entry<String, Long> lastEntry : previousFiles.entrySet()) {
			if (!currentFiles.containsKey(lastEntry.getKey())) {
				resourceDeleted(new File(lastEntry.getKey()));
			}
		}
	}
}
