package ch.zhaw.simulation.startup;

import java.io.File;
import java.io.IOException;

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
public class SimulationMain {
	private static String openfile;

	public static void main() {
		Settings settings = new FileSettings("settings.ini");

		String lookAndFeel = settings.getSetting("ui.look-and-feel", null);

		if (lookAndFeel == null && !OS.isMacOSX()) {
			lookAndFeel = "nimbus";
		}

		if (OS.isMacOSX()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Simulation");
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

		// install netbeans Folderchooser for JFilechooser
		FolderChooserUi m = new FolderChooserUi();
		m.installUI();

		ApplicationControl app = new ApplicationControl();
		app.start(settings, openfile);
	}

	public static void main(String[] args) {
		try {
			Config.initConifg("Simulation");
		} catch (IOException e) {
			Messagebox.showError(null, "Konfiguration fehlt", "<html>Die Datei «" + Config.getConfigFile() + "» konnte nicht geöffnet / gelesen werden!<br>Start nicht möglich.</html>");
			System.exit(0);
		}

		// Global error handler, catches all exceptions within swing event loop
		Errorhandler.registerAwtErrorHandler();

		try {
			for (String s : args) {
				File f = new File(s);
				if (f.exists() && f.canRead()) {
					openfile = s;
				} else {
					System.err.println("Could not open file: " + s);
				}
			}

			main();
		} catch (Exception e) {
			Errorhandler.showError(e);
		}
	}

}
