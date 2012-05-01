package ch.zhaw.simulation.util.gui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

public class ImageMirrow {
	// TODO !!! REMOVE !! 
	
	public static BufferedImage horizontalMirror(BufferedImage img) {
		BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(),
				img.getType());
		
		int h = img.getHeight();
		int w = img.getWidth();
		
		int[] rgbArray = new int[h];
		
		for(int x = 0; x < w; x++) {
			rgbArray = img.getRGB(x, 0, 1, h, rgbArray, 0, 1);
			res.setRGB(w - x - 1, 0, 1, h, rgbArray, 0, 1);
		}
		
		return res;
	}
	
	public static BufferedImage verticalMirror(BufferedImage img) {
		BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(),
				img.getType());
		
		int h = img.getHeight();
		int w = img.getWidth();
		
		int[] rgbArray = new int[h];
		
		for(int y = 0; y < h; y++) {
			rgbArray = img.getRGB(0, y, w, 1, rgbArray, 0, w);
			res.setRGB(0, h - y - 1, w, 1, rgbArray, 0, w);
		}
		
		return res;
	}
	
	public static BufferedImage rotateImage(BufferedImage image, int degrees) {
		int w = image.getWidth();
		int h = image.getHeight();

		if (degrees == 90 || degrees == 270) {
			h = image.getWidth();
			w = image.getHeight();
		}

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = (Graphics2D) img.getGraphics();
		AffineTransform origXform = g2d.getTransform();
		AffineTransform newXform = (AffineTransform) (origXform.clone());
		// center of rotation is center of the panel
		int x = image.getWidth() / 2;
		int y = image.getHeight() / 2;
		newXform.rotate(Math.toRadians(degrees), x, y);
		g2d.setTransform(newXform);
		// draw image centered in panel
		g2d.drawImage(image, 0, 0, new JLabel());
		g2d.setTransform(origXform);

		return img;
	}
}
