package org.netbeans.swing.dirchooser;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		FolderChooserUi m = new FolderChooserUi();
		m.installUI();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				System.out.println(FolderChooserUi.isInstalled());
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fc.showOpenDialog(null);
				System.out.println(returnVal);
			}
		});

	}
}
