package ch.zhaw.simulation.xyviewer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.zhaw.simulation.xyviewer.model.XYResult;
import ch.zhaw.simulation.xyviewer.model.XYResultStep;
import ch.zhaw.simulation.xyviewer.model.dummy.XYResultDummy;

public class XYViewerTest {
	public static void main(String[] args) throws IOException {
		String outputPath = "c:\\tmp\\";

		XYViewer viewer = new XYViewer();

		XYResult result = new XYResultDummy();

		Dimension s = result.getModelSize();
		BufferedImage img = new BufferedImage(s.width, s.height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = img.createGraphics();

		viewer.init(result);

		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			System.out.println(i + " from " + count);
			XYResultStep step = result.getStep(i);
			viewer.draw(g, step);
			ImageIO.write(img, "PNG", new File(outputPath + "img_" + String.format("%03d", i) + ".png"));
		}

		System.out.println("fertig!");

		g.dispose();
	}

}
