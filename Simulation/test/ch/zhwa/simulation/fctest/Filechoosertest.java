package ch.zhwa.simulation.fctest;

import java.io.File;

import ch.zhaw.simulation.sysintegration.SimFileFilter;
import ch.zhaw.simulation.sysintegration.filechooser.FilechooserException;
import ch.zhaw.simulation.sysintegration.filechooser.script.LinuxFilechooser;

public class Filechoosertest {
	public static void main(String[] args) throws FilechooserException {
		// testLinuxOpen();
		testLinuxSave();
	}

	public static void testLinuxOpen() throws FilechooserException {
		LinuxFilechooser fc = new LinuxFilechooser();
		File file = fc.showOpenDialog(null, new SimFileFilter() {

			@Override
			public String getDescription() {
				return "Simulationsdateien";
			}

			@Override
			public boolean accept(File f) {
				return true;
			}

			@Override
			public String getExtension() {
				return ".simz";
			}

		}, "/tmp/");

		System.out.println("->" + file);
	}

	public static void testLinuxSave() throws FilechooserException {
		LinuxFilechooser fc = new LinuxFilechooser();
		File file = fc.showSaveDialog(null, new SimFileFilter() {

			@Override
			public String getDescription() {
				return "Simulationsdateien";
			}

			@Override
			public boolean accept(File f) {
				return true;
			}

			@Override
			public String getExtension() {
				return ".simz";
			}

		}, "/tmp/");

		System.out.println("->" + file);
	}
}
