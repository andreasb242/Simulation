package ch.zhaw.simulation.diagram.export;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

public class LegendExportHtml {
	public LegendExportHtml() {
	}

	private static String encodeHtml(String text) {
		StringBuilder buf = new StringBuilder();
		StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				buf.append("&lt;");
			} else if (character == '>') {
				buf.append("&gt;");
			} else if (character == '&') {
				buf.append("&amp;");
			} else {
				buf.append(character);
			}
			character = iterator.next();
		}
		return buf.toString();
	}

	public static String getColorHex(Color color) {
		final String ZEROES = "000000";
		String s = Integer.toString(color.getRGB() & 0x00ffffff, 16);
		return s.length() <= 6 ? ZEROES.substring(s.length()) + s : s;
	}

	public void exportLegend(String file, JFreeChart chart, String name) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "UTF8");

		out.write("<!doctype html>\n");
		out.write("<html lang=\"de\">");
		out.write("<head>");
		out.write("<meta charset=\"utf-8\">");
		out.write("\t<title>" + encodeHtml(name) + " - Legende</title>");
		out.write("\t<meta name=\"generator\" content=\"(AB)² Simulation\">");
		out.write("</head>");
		out.write("<body>");

		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();

		for (int i = 0; i < dataset.getSeriesCount(); i++) {

			out.write("\t<span style=\"color: #" + getColorHex((Color) renderer.lookupSeriesPaint(i)) + "\">&bull;</span> "
					+ encodeHtml(dataset.getSeries(i).getKey().toString()) + "<br />\n");
		}

		out.write("</body>");
		out.write("</html>");

		out.close();
	}

}
