package ch.zhaw.simulation.widgets.zoom;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class DiagrammSliderUi extends BasicSliderUI {

	static BufferedImage imgSlider = getIcon("slider.png");

	static BufferedImage imgTrackLeft = getIcon("track.left.png");
	static BufferedImage imgTrackCenter = getIcon("track.center.png");
	static BufferedImage imgTrackRight = getIcon("track.right.png");

	public DiagrammSliderUi(JSlider slider) {
		super(slider);
	}

	static BufferedImage getIcon(String name) {
		try {
			return ImageIO.read(DiagrammSliderUi.class.getResource(name));
		} catch (IOException e) {
			System.out.println("Icon " + name + " not found");
			return null;
		}
	}

	protected Dimension getThumbSize() {
		return new Dimension(imgSlider.getWidth(), imgSlider.getHeight());
	}

	protected void calculateTrackRect() {
		int centerSpacing = 0;
		centerSpacing = thumbRect.height;
		if (slider.getPaintTicks())
			centerSpacing += getTickLength();
		trackRect.x = contentRect.x + trackBuffer;
		trackRect.y = contentRect.y + (contentRect.height - centerSpacing - 1) / 2;
		trackRect.width = contentRect.width - (trackBuffer * 2);
		trackRect.height = thumbRect.height;

	}

	@Override
	public void paintTrack(Graphics g) {
		int imgY = thumbRect.y + (thumbRect.height / 2) - 2;
		int xRight = trackRect.x + trackRect.width - imgTrackRight.getWidth();

		g.drawImage(imgTrackLeft, trackRect.x, imgY, slider);

		for (int x = trackRect.x + imgTrackLeft.getWidth(); x < xRight; x++) {
			g.drawImage(imgTrackCenter, x, imgY, slider);
		}

		g.drawImage(imgTrackRight, xRight, imgY, slider);
	}

	@Override
	public void paintThumb(Graphics g) {
		g.drawImage(imgSlider, thumbRect.x, thumbRect.y, slider);
	}
}
