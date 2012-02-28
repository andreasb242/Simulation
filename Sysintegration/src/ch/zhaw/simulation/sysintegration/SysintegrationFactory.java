package ch.zhaw.simulation.sysintegration;

import org.jdesktop.swingx.util.OS;

import ch.zhaw.simulation.sysintegration.linux.LinuxSysintegration;
import ch.zhaw.simulation.sysintegration.windows.WindowsSysintegration;

public class SysintegrationFactory {

	private static Sysintegration cached = null;

	/**
	 * Creates the sysintegration objects depending on the operatings system
	 * 
	 * @return A new instance of Sysintegration
	 */
	public static synchronized Sysintegration createSysintegration() {
		if (cached != null) {
			return cached;
		}

		if (OS.isMacOSX()) {
			cached = new WindowsSysintegration();
		}
		if (OS.isLinux()) {
			cached = new LinuxSysintegration();
		}
		if (OS.isWindows()) {
			cached = new WindowsSysintegration();
		}

		cached = new Sysintegration();

		return cached;
	}

}
