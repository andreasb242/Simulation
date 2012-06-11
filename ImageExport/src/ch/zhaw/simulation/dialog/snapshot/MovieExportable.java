package ch.zhaw.simulation.dialog.snapshot;

import java.io.File;
import java.io.IOException;

public interface MovieExportable {
	
	public int getCount();
	public void export(int pos, File file) throws Exception;
	public int getFps();
	
}
