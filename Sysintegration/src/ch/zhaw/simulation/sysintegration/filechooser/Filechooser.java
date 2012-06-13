package ch.zhaw.simulation.sysintegration.filechooser;

import java.awt.Window;
import java.io.File;

import ch.zhaw.simulation.sysintegration.SimFileFilter;

public interface Filechooser {
	public abstract File showSaveDialog(Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException;

	public abstract File showOpenDialog(Window parent, SimFileFilter filefilter, String lastSavePath) throws FilechooserException;
}
