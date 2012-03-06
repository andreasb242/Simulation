package ch.zhaw.simulation.sysintegration.windows;

import ch.zhaw.simulation.sysintegration.Sysintegration;

public class WindowsSysintegration extends Sysintegration {

	public WindowsSysintegration() {
	}

	@Override
	protected void initBookmarks() {
		this.bookmarks = new WindowsBookmarks();
	}

}
