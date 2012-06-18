package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.GridBagManager;

public class LibraryPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private GridBagManager gbm;
	private int yCoord = 12;

	public LibraryPanel() {
		this.gbm = new GridBagManager(this);
		setOpaque(false);

		JLabel lbLib = new TitleLabel("Verwendete Libraries");
		gbm.setX(1).setY(0).setWeightY(0).setWidth(2).setComp(lbLib);

		addLib("SwingX", "http://swinglabs.org/");
		addLib("JXLayer", "http://http://java.net/projects/jxlayer");
		addLib("<html>Jep Java (angepasst)<br> - Math Expression Parser</html>", "http://sourceforge.net/projects/jep/");
		addLib("Dirchooser from Netbeans Project", "http://netbeans.org/");
		addLib("JFreeChart", "http://www.jfree.org/");
		addLib("Mac Widgets (Codefragmente)", "http://explodingpixels.wordpress.com/2008/09/14/mac-widgets-for-java-091/");

		JLabel lbIcons = new TitleLabel("Verwendete Icons");
		gbm.setInsets(new Insets(30, 5, 5, 5));
		gbm.setX(1).setY(100).setWeightY(0).setWidth(2).setComp(lbIcons);

		yCoord = 101;

		addLib("Faenza", "http://tiheum.deviantart.com/art/Faenza-Icons-173323228");
		addLib("Humanity (Ubuntu / Canonical)", "https://launchpad.net/humanity");
		addLib("Icons wurden teilweise angepasst.", null);

	}

	private void addLib(String name, String url) {
		gbm.setX(1).setInsets(new Insets(5, 24, 5, 5)).setY(yCoord).setWeightY(0).setComp(new JLabel(name));

		if (url != null) {
			gbm.setX(2).setY(yCoord++).setWeightY(0).setComp(new UrlLabel(url));
		}
	}
}
