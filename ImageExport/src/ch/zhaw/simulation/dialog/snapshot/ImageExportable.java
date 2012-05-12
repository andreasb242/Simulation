package ch.zhaw.simulation.dialog.snapshot;

import java.io.File;
import java.io.IOException;

public interface ImageExportable {
	public void exportToClipboard(boolean onlySelection);

	public void export(boolean onlySelection, String format, File file) throws IOException;

	/**
	 * @return true: A checkbox for "only selection will be showed"
	 */
	public boolean supportsSelection();
}
