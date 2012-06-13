package ch.zhaw.simulation.sysintegration.windows;

import ch.zhaw.simulation.sysintegration.Sysintegration;
import ch.zhaw.simulation.sysintegration.filechooser.AbstractFilechooser;
import ch.zhaw.simulation.sysintegration.filechooser.script.WindowsFilechooser;

public class WindowsSysintegration extends Sysintegration {

	public WindowsSysintegration() {
	}

	@Override
	protected AbstractFilechooser createFilechooser() {
		return new WindowsFilechooser();
	}

	@Override
	protected void initBookmarks() {
		this.bookmarks = new WindowsBookmarks();
	}

}
