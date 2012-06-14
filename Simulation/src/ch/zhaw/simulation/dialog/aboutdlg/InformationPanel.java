package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.GridBagManager;

public class InformationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private GridBagManager gbm;

	public InformationPanel() {
		this.gbm = new GridBagManager(this);
		setOpaque(false);

		JLabel lbAuthors = new TitleLabel("Autoren");
		gbm.setX(0).setY(6).setWidth(2).setWeightY(0).setWidth(2).setComp(lbAuthors);

		gbm.setX(0).setY(7).setWidth(2).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0)
				.setComp(new JLabel("Andreas Bachmann © 2012 - bachman0" + ((char) 64) + "students.zhaw.ch"));
		gbm.setX(0).setY(8).setWidth(2).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0)
				.setComp(new JLabel("Andreas Butti  © 2012 - andreasbutti" + ((char) 64) + "gmail.com"));

		JLabel lbBA = new JLabel("<html><b>BA - Betreuung</b> (2012)</html>");
		gbm.setInsets(new Insets(30, 5, 5, 5));
		gbm.setX(0).setY(50).setWidth(2).setWeightY(0).setWidth(2).setComp(lbBA);

		gbm.setX(0).setY(51).setWidth(2).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0).setComp(new JLabel("Dr. Stephan Scheidegger"));
		gbm.setX(0).setY(52).setWidth(2).setInsets(new Insets(5, 24, 5, 5)).setWeightY(0).setComp(new JLabel("Dr. Rudolf Marcel Füchslin"));

		JLabel lbLicense = new TitleLabel("Lizenz");
		gbm.setInsets(new Insets(30, 5, 5, 5));
		gbm.setX(0).setY(100).setWeightY(0).setWidth(2).setComp(lbLicense);
		gbm.setX(0).setY(101).setWeightY(0).setInsets(new Insets(5, 24, 5, 0))
				.setComp(new JLabel("<html>GPL: GNU General Public License<br><i>This is free software</i></html>"));
	}
}
