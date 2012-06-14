package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.GridBagManager;

public class SystemPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private GridBagManager gbm;

	public SystemPanel() {
		this.gbm = new GridBagManager(this);
		setOpaque(false);

		JLabel lbMemory = new TitleLabel("Speicher");
		gbm.setX(1).setY(6).setWeightY(0).setWidth(2).setComp(lbMemory);

		String usedMem = formatMemoryUsage(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

		String allocMem = formatMemoryUsage(Runtime.getRuntime().totalMemory());

		String totalMem = formatMemoryUsage(Runtime.getRuntime().maxMemory());

		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(7).setWeightY(0).setComp(new JLabel("Benutzt:"));
		gbm.setX(2).setY(7).setWeightY(0).setComp(new JLabel(usedMem));

		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(8).setWeightY(0).setComp(new JLabel("Reserviert:"));
		gbm.setX(2).setY(8).setWeightY(0).setComp(new JLabel(allocMem));

		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(9).setWeightY(0).setComp(new JLabel("Max. verfügbar:"));
		gbm.setX(2).setY(9).setWeightY(0).setComp(new JLabel(totalMem));

		JLabel lbJava = new TitleLabel("Java");
		gbm.setInsets(new Insets(30, 5, 5, 5));
		gbm.setX(1).setY(100).setWeightY(0).setWidth(2).setComp(lbJava);

		gbm.setX(1).setY(101).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0).setComp(new JLabel("Java Version"));
		gbm.setX(2).setY(101).setWeightY(0).setComp(new JLabel(System.getProperty("java.version")));
		
		gbm.setX(1).setY(102).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0).setComp(new JLabel("Java Vendor"));
		gbm.setX(2).setY(102).setWeightY(0).setComp(new JLabel(System.getProperty("java.vendor")));


		JLabel lbOS = new TitleLabel("Betriebssystem");
		gbm.setInsets(new Insets(30, 5, 5, 5));
		gbm.setX(1).setY(105).setWeightY(0).setWidth(2).setComp(lbOS);
		
		gbm.setX(1).setY(106).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0).setComp(new JLabel("Name"));
		gbm.setX(2).setY(106).setWeightY(0).setComp(new JLabel(System.getProperty("os.name")));
		
		gbm.setX(1).setY(107).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0).setComp(new JLabel("Architektur"));
		gbm.setX(2).setY(107).setWeightY(0).setComp(new JLabel(System.getProperty("os.arch")));
		
		gbm.setX(1).setY(108).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0).setComp(new JLabel("Version"));
		gbm.setX(2).setY(108).setWeightY(0).setComp(new JLabel(System.getProperty("os.version")));
		
	}

	/**
	 * Formatiert den long in Speichergrössen (MB, KB, B)
	 * 
	 * @param m
	 *            Speicher
	 * @return Formatierte Ausgabe
	 */
	public static String formatMemoryUsage(long m) {
		if (m > 1024 * 1024) {
			return (m / 1024 / 1024) + "MB";
		}
		if (m > 1024) {
			return (m / 1024) + "KB";
		}
		return m + "B";
	}
}
