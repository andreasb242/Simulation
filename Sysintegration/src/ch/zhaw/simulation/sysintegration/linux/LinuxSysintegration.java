package ch.zhaw.simulation.sysintegration.linux;

import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.linux.bookmarks.GtkBookmarks;

public class LinuxSysintegration extends Sysintegration {

	public LinuxSysintegration() {
	}
	
	@Override
	protected void initBookmarks() {
		this.bookmarks = new GtkBookmarks();
	}
}
