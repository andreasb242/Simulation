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

import butti.javalibs.config.Config;
import butti.javalibs.gui.GridBagManager;
import ch.zhaw.simulation.icon.IconLoader;
import ch.zhaw.simulation.util.gui.HeaderPanel;

public class AboutHeader extends HeaderPanel {
	private static final long serialVersionUID = 1L;

	public AboutHeader() {
		GridBagManager gbm = new GridBagManager(this);

		String version = getVersion();

		gbm.setX(0).setY(0).setInsets(new Insets(10, 5, 5, 20))
				.setComp(new JLabel("<html><b>Information über <font face=\"Serif\">(AB)²</font> Simulation</b> Build " + version + "</html>"));
		gbm.setX(0).setY(1).setInsets(new Insets(0, 24, 20, 20)).setComp(new UrlLabel(Config.get("homepage")));

		ImageIcon icon = IconLoader.getIcon("simulation", 128);
		gbm.setX(10).setY(0).setHeight(200).setInsets(new Insets(0, 0, 0, 12)).setComp(new JLabel(icon));
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
