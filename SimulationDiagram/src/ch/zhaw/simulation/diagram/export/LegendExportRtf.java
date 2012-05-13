package ch.zhaw.simulation.diagram.export;

import static com.tutego.jrtf.Rtf.rtf;
import static com.tutego.jrtf.RtfHeader.color;
import static com.tutego.jrtf.RtfPara.p;
import static com.tutego.jrtf.RtfText.bold;
import static com.tutego.jrtf.RtfText.color;
import static com.tutego.jrtf.RtfText.picture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import com.tutego.jrtf.Rtf;
import com.tutego.jrtf.RtfPara;
import com.tutego.jrtf.RtfPicture.PictureType;
import com.tutego.jrtf.RtfUnit;

public class LegendExportRtf {
	public LegendExportRtf() {
	}

	public void exportLegend(String path, JFreeChart chart, String name) throws IOException {
		Rtf rtf = rtf();

		Vector<RtfPara> contents = new Vector<RtfPara>();
		contents.add(p(bold(name)));

		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();

		int colorId = 0;

		// Bullet legend
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			Color c = (Color) renderer.lookupSeriesPaint(i);

			rtf.header(color(c.getRed(), c.getGreen(), c.getBlue()).at(colorId));

			String n = dataset.getSeries(i).getKey().toString();

			contents.add(p(color(colorId, "•"), "\t" + n));

			colorId++;
		}

		contents.add(p("\n"));
		contents.add(p(bold(name)));

		// Image legend
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			Color c = (Color) renderer.lookupSeriesPaint(i);
			String n = dataset.getSeries(i).getKey().toString();

			Stroke stroke = renderer.getSeriesStroke(i);

			if (stroke == null) {
				stroke = renderer.getBaseStroke();
			}

			BufferedImage img = createImage(c, stroke);
			ByteArrayBuffer buffer = new ByteArrayBuffer(2048);
			ImageIO.write(img, "PNG", buffer);

			contents.add(
				p(
					picture(buffer.newInputStream())
					// 300 dpi
					.size(img.getWidth() / 300.0, img.getHeight() / 300.0, RtfUnit.INCH)
					.type(PictureType.PNG),
					"\t" + n
				)
			);
		}

		rtf.section(contents);
		rtf.out(new FileWriter(path));
	}

	private BufferedImage createImage(Color c, Stroke stroke) {
		int w = 300;
		int h = 100;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();

		g.setColor(c);
		g.setStroke(stroke);

		int y = h / 2;
		g.drawLine(0, y, w, y);

		g.dispose();
		return img;
	}

}
