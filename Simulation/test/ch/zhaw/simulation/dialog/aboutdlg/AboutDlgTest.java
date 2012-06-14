package ch.zhaw.simulation.dialog.aboutdlg;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import butti.javalibs.config.Config;

public class AboutDlgTest {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		
		Config.initConfig("(AB)²Simulation", "AB2Simulation");

		
		AboutDialog dlg = new AboutDialog(null);
		dlg.setVisible(true);
	}
}
