package ch.zhaw.simulation.diagram;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;

import ch.zhaw.simulation.dialog.snapshot.ImageExportable;
import ch.zhaw.simulation.vector.VectorExport;

public class ChartExporter implements ImageExportable {

	private ChartPanel chartPanel;

	public ChartExporter(ChartPanel chartPanel) {
		this.chartPanel = chartPanel;
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
	}

	@Override
	public boolean supportsSelection() {
		return false;
	}

}
