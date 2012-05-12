package ch.zhaw.simulation.diagram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import ch.zhaw.simulation.dialog.snapshot.ImageExportable;
import ch.zhaw.simulation.vector.VectorExport;

public class ChartExporter implements ImageExportable {

	private ChartPanel chartPanel;
	private String name;

	public ChartExporter(ChartPanel chartPanel, String name) {
		this.chartPanel = chartPanel;
		this.name = name;
	}

	@Override
	public void exportToClipboard(boolean onlySelection) {
		chartPanel.actionPerformed(new ActionEvent(this, 0, ChartPanel.COPY_COMMAND));
	}

	@Override
	public void export(boolean onlySelection, String format, File file) throws IOException {
		if ("PNG".equals(format)) {
			ChartUtilities.saveChartAsPNG(file, chartPanel.getChart(), chartPanel.getWidth(), chartPanel.getHeight());
		} else {
			VectorExport ex = new VectorExport(new FileOutputStream(file), new Dimension(chartPanel.getWidth(), chartPanel.getHeight()), format);

			Graphics2D g = ex.getGraphics();
			chartPanel.getChart().draw(g, new Rectangle2D.Double(0, 0, chartPanel.getWidth(), chartPanel.getHeight()), null, null);
			g.dispose();

			ex.close();
		}

		exportHtmlLegend(file.getAbsolutePath() + ".legende.html", chartPanel.getChart());
	}

	private static String encodeHtml(String text) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public static String getColorHex(Color color) {
		final String ZEROES = "000000";
		String s = Integer.toString(color.getRGB() & 0x00ffffff, 16);
		return s.length() <= 6 ? ZEROES.substring(s.length()) + s : s;
	}

	private void exportHtmlLegend(String file, JFreeChart chart) throws IOException {
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

	@Override
	public boolean supportsSelection() {
		return false;
	}

}
