package ch.zhaw.simulation.dialog.aboutdlg;

import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXLabel;

import butti.javalibs.controls.TitleLabel;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.util.gui.HeaderPanel;

public class AboutHeader extends HeaderPanel {
	private static final long serialVersionUID = 1L;

	private ImageIcon icon;

	public AboutHeader() {
		GridBagManager gbm = new GridBagManager(this);

		this.icon = IconLoader.getIcon("simulation", 128);

		String version = getVersion();
		
		gbm.setX(0).setY(0).setInsets(new Insets(10, 5, 5, 20)).setComp(new TitleLabel("<html>Information über <font face=\"Serif\">(AB)²</font> Simulation Build " + version + "</html>"));
		gbm.setX(0).setY(1).setInsets(new Insets(0, 24, 20, 20)).setComp(new UrlLabel("http://sourceforge.net/projects/!!TODO!!/"));
		
		gbm.setX(0).setY(10).setWeightY(0).setInsets(new Insets(10, 5, 5, 20)).setComp(new TitleLabel("Autoren"));

		gbm.setX(0).setY(11).setWeightY(0).setInsets(new Insets(5, 24, 5, 0)).setComp(new JLabel("<html>Andreas Bachmann © 2012</html>"));

		gbm.setX(0).setY(12).setWeightY(0).setInsets(new Insets(5, 24, 5, 0)).setComp(new JLabel("<html>Andreas Butti  © 2012<br>andreasbutti"+((char)64)+"gmail.com</html>"));

		gbm.setX(0).setY(100).setInsets(new Insets(20, 24, 20, 0)).setComp(new JXLabel(
				"<html>BA - Betreuung<br>" +
				"	• Dr. Stephan Scheidegger<br>" +
				"	• Dr. Rudolf Marcel Füchslin</html>"
				));

		gbm.setX(0).setY(110).setWeightY(0).setInsets(new Insets(10, 5, 5, 10)).setComp(new TitleLabel("Lizenz"));

		gbm.setX(0).setY(111).setWeightY(0).setInsets(new Insets(5, 24, 5, 0)).setComp(new JLabel("<html>GPL: GNU General Public License<br><i>This is free software</i></html>"));

		
		gbm.setX(10).setY(0).setHeight(200).setInsets(new Insets(0, 0, 0, 12)).setComp(new JXLabel(this.icon));
		
	}

	public static String getManifestInfo() {
		try {
			Enumeration<?> resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
			while (resEnum.hasMoreElements()) {
				try {
					URL url = (URL) resEnum.nextElement();
					InputStream is = url.openStream();
					if (is != null) {
						Manifest manifest = new Manifest(is);
						Attributes mainAttribs = manifest.getMainAttributes();
						String version = mainAttribs.getValue("Implementation-Version");
						if (version != null) {
							return version;
						}
					}
				} catch (Exception e) {
					// Silently ignore wrong manifests on classpath?
				}
			}
		} catch (IOException e1) {
			// Silently ignore wrong manifests on classpath?
		}
		return null;
	}

	private String getVersion() {
		String version = getManifestInfo();
		if (version != null) {
			return version;
		}
		return "$Unknonwn$";
	}
}
