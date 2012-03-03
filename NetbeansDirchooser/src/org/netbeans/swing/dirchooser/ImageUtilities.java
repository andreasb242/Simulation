package org.netbeans.swing.dirchooser;

import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageUtilities {

	public static Icon loadImageIcon(String filename, boolean b) {
		InputStream in = ImageUtilities.class.getClassLoader().getResourceAsStream(filename);

		if (in == null) {
			throw new RuntimeException("image not found: " + filename);
		}
		try {
			return new ImageIcon(ImageIO.read(in));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
