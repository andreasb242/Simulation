

import java.io.File;

import javax.swing.UIManager;

import org.jdesktop.swingx.util.OS;

import ch.zhaw.simulation.editor.control.AbstractEditorControl;
import ch.zhaw.simulation.editor.xy.XYEditor;
import ch.zhaw.simulation.frame.AbstractFrame;
import ch.zhaw.simulation.frame.MainFrame;

import butti.javalibs.config.Settings;
import butti.javalibs.config.FileSettings;
import butti.javalibs.errorhandler.Errorhandler;

public class TestMain {
	private static String openfile;

	public static void main() {
		Settings settings = new FileSettings("settings.ini");

		String lookAndFeel = settings.getSetting("ui.look-and-feel", null);

		if (lookAndFeel == null && !OS.isMacOSX()) {
			lookAndFeel = "nimbus";
		}

		try {
			if ("metal".equals(lookAndFeel)) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else if ("nimbus".equals(lookAndFeel)) {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			} else {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		} catch (Exception e) {
			Errorhandler.logError(e);
		}

		System.out.println(openfile);
		
		AbstractEditorControl editor = new XYEditor();
		
		AbstractFrame frame = new MainFrame(editor);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
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
