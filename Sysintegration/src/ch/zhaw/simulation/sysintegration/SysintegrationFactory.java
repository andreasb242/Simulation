package ch.zhaw.simulation.sysintegration;

import org.jdesktop.swingx.util.OS;

import ch.zhaw.simulation.sysintegration.linux.LinuxSysintegration;
import ch.zhaw.simulation.sysintegration.windows.WindowsSysintegration;

public class SysintegrationFactory {

	/**
	 * Creates the sysintegration objects depending on the operatings system
	 * 
	 * @return A new instance of Sysintegration
	 */
	public static Sysintegration createSysintegration() {
		if (OS.isMacOSX()) {
			return new WindowsSysintegration();
		}
		if (OS.isLinux()) {
			return new LinuxSysintegration();
		}
		if (OS.isWindows()) {
			return new WindowsSysintegration();
		}

		return new Sysintegration();
	}

}
