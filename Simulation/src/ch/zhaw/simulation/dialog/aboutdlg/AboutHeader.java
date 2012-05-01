package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.Insets;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXLabel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.util.gui.HeaderPanel;

public class AboutHeader extends HeaderPanel {
	private static final long serialVersionUID = 1L;

	private ImageIcon icon;

	public AboutHeader() {
		GridBagManager gbm = new GridBagManager(this, true);

		this.icon = IconLoader.getIcon("simulation", 128);

		gbm.setX(0).setY(0).setInsets(new Insets(10, 5, 5, 20)).setComp(new TitleLabel("<html>Information über <font face=\"Serif\">(AB)²</font> Simulation</html>"));
		gbm.setX(0).setY(1).setInsets(new Insets(0, 24, 20, 20)).setComp(new UrlLabel("http://sourceforge.net/projects/!!TODO!!/"));
		
		gbm.setX(0).setY(10).setWeightY(0).setInsets(new Insets(10, 5, 5, 20)).setComp(new TitleLabel("Autoren"));

		gbm.setX(0).setY(11).setWeightY(0).setInsets(new Insets(5, 24, 5, 0)).setComp(new JXLabel(
				"<html>Andreas Bachmann © 2012 - ZHAW<br>" + 
				"	• Codegeneration<br>" +
				"	• Diagrammdarstellung</html>"
				));

		gbm.setX(0).setY(12).setWeightY(0).setInsets(new Insets(5, 24, 5, 0)).setComp(new JXLabel(
				"<html>Andreas Butti  © 2012 - ZHAW<br>" +
				"	• Simulationseditor<br>" +
				"	• Dateihandling</html>"
				));

		gbm.setX(0).setY(100).setInsets(new Insets(20, 24, 20, 0)).setComp(new JXLabel(
				"<html>BA - Betreuung<br>" +
				"	• Dr. Stephan Scheidegger<br>" +
				"	• Dr. Rudolf Marcel Füchslin</html>"
				));

		gbm.setX(0).setY(110).setWeightY(0).setInsets(new Insets(10, 5, 5, 10)).setComp(new TitleLabel("Lizenz"));

		gbm.setX(0).setY(111).setWeightY(0).setInsets(new Insets(5, 24, 5, 0)).setComp(new JXLabel(
				"GPL: GNU General Public License"
				));

		
		gbm.setX(10).setY(0).setHeight(200).setInsets(new Insets(0, 0, 0, 12)).setComp(new JXLabel(this.icon));
		
	}
}
