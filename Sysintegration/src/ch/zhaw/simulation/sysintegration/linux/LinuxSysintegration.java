package ch.zhaw.simulation.sysintegration.linux;

import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.filechooser.AbstractFilechooser;
import ch.zhaw.simulation.sysintegration.filechooser.script.LinuxFilechooser;
import ch.zhaw.simulation.sysintegration.linux.bookmarks.GtkBookmarks;

public class LinuxSysintegration extends Sysintegration {

	public LinuxSysintegration() {
	}

	protected AbstractFilechooser createFilechooser() {
		return new LinuxFilechooser();
	}

	@Override
	protected void initBookmarks() {
		this.bookmarks = new GtkBookmarks();
	}
}
