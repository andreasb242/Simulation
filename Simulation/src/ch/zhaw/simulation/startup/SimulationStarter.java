package ch.zhaw.simulation.startup;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingx.util.OS;
import org.netbeans.swing.dirchooser.FolderChooserUi;

import butti.javalibs.config.Config;
import butti.javalibs.config.FileSettings;
import butti.javalibs.config.Settings;
import butti.javalibs.errorhandler.Errorhandler;
import butti.javalibs.gui.messagebox.Messagebox;
import ch.zhaw.simulation.app.ApplicationControl;

/**
 * Initializes the application
 * 
 * @author Andreas Butti
 */
public class SimulationStarter {
	private static String openfile;
	private static Vector<String> parameter = new Vector<String>();

	public static void main() {
		final Settings settings = new FileSettings("settings.ini");

		String lookAndFeel = settings.getSetting("ui.look-and-feel", null);

		if (lookAndFeel == null && !OS.isMacOSX()) {
			lookAndFeel = "nimbus";
		}

		if (OS.isMacOSX()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "(AB)² Simulation");
		}

		try {
			if ("metal".equals(lookAndFeel)) {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} else if ("nimbus".equals(lookAndFeel)) {

				// ca. 100ms faster than only setLookAndFeel
				System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			Errorhandler.logError(e);
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// install netbeans Folderchooser for JFilechooser
				FolderChooserUi m = new FolderChooserUi();
				m.installUI();

				ApplicationControl app = new ApplicationControl();
				app.start(settings, parameter, openfile);
				app.updateTitle();

			}
		});
	}

	public static void main(String[] args) {
		try {
			Config.initConfig("(AB)²Simulation", "AB2Simulation");
		} catch (IOException e) {
			Messagebox.showError(null, "Konfiguration fehlt", "<html>Die Datei «" + Config.getConfigFile()
					+ "» konnte nicht geöffnet / gelesen werden!<br>Start nicht möglich.</html>");

			e.printStackTrace();
			File f = new File(Config.getConfigFile());
			System.out.println("Asolute path: \"" + f.getAbsolutePath() + "\"");

			System.exit(0);
		}

		// Global error handler, catches all exceptions within swing event loop
		Errorhandler.registerAwtErrorHandler();

		try {
			for (String s : args) {
				if (s.startsWith("--")) {
					parameter.add(s);
				} else {
					File f = new File(s);
					if (f.exists() && f.canRead()) {
						openfile = s;
					} else {
						System.err.println("Could not open file: " + s);
					}
				}
			}

			main();
		} catch (Exception e) {
			Errorhandler.showError(e);
		}
	}

}
