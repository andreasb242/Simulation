package ch.zhaw.simulation.sysintegration;

import javax.swing.filechooser.FileFilter;

public abstract class SimFileFilter extends FileFilter {
	public abstract String getExtension();
}
