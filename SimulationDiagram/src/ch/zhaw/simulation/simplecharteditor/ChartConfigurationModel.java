package ch.zhaw.simulation.simplecharteditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;

import ch.zhaw.simulation.diagram.DiagramStrokeFactory;
import ch.zhaw.simulation.diagram.persist.DiagramConfiguration;

public class ChartConfigurationModel {
	public enum ColorState {
		NOT_SET, // Unknown, Custom formated or something else
		COLORED, // The Charts are automatically colored
		BLACK_WHITE // The Charts are Black & White
	}

	private JFreeChart chart;
	private Font lastUsedFont = null;
	private DiagramConfiguration diagramConfig;

	private ColorState colorState = null;

	public ChartConfigurationModel(JFreeChart chart, DiagramConfiguration diagramConfig) {
		this.chart = chart;
		this.diagramConfig = diagramConfig;

		String color = diagramConfig.get("charteditor.style", "COLORED");

		try {
			this.colorState = ColorState.valueOf(color);
		} catch (Exception e) {
		}
		if (this.colorState == null) {
			this.colorState = ColorState.NOT_SET;
		}
	}

	public String getTitleText() {
		TextTitle title = this.chart.getTitle();
		if (title == null) {
			return "";
		}
		return title.getText();
	}

	public void setTitleText(String text) {
		if (text == null) {
			text = "";
		}

		TextTitle title = getTitle();

		title.setVisible(!text.isEmpty());
		title.setText(text);
	}

	private TextTitle getTitle() {
		TextTitle title = this.chart.getTitle();

		if (title == null) {
			title = new TextTitle();
			this.chart.setTitle(title);
		}
		return title;
	}

	public String getXAxisText() {
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis axis = plot.getDomainAxis();

		return axis.getLabel();
	}

	public void setXAxisText(String text) {
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis axis = plot.getDomainAxis();

		axis.setLabel(text);
	}

	public String getYAxisText() {
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis axis = plot.getRangeAxis();

		return axis.getLabel();
	}

	public void setYAxisText(String text) {
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis axis = plot.getRangeAxis();

		axis.setLabel(text);
	}

	public ColorState getColorState() {
		return colorState;
	}

	public void setColorState(ColorState colorState) {
		this.colorState = colorState;

		if (this.colorState == ColorState.COLORED) {
			clolorizeColor();
		} else if (this.colorState == ColorState.BLACK_WHITE) {
			colorizeBlackAndWhite();
		} // else nothing to do

		this.diagramConfig.set("charteditor.style", this.colorState.toString());
	}

	private void clolorizeColor() {
		XYPlot plot = (XYPlot) chart.getPlot();
		AbstractXYItemRenderer r = (AbstractXYItemRenderer) plot.getRenderer();
		int count = plot.getDataset().getSeriesCount();

		BasicStroke s = DiagramStrokeFactory.createStroke();

		/**
		 * Reset all values
		 */
		plot.setDrawingSupplier(new DefaultDrawingSupplier(), false);

		for (int i = 0; i < count; i++) {
			r.setSeriesStroke(i, s, false);
		}
		r.clearSeriesPaints(true);
	}

	private void colorizeBlackAndWhite() {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYItemRenderer r = plot.getRenderer();
		int count = plot.getDataset().getSeriesCount();

		// Alle Linien schwarz
		for (int i = 0; i < count; i++) {
			r.setSeriesPaint(i, Color.BLACK);
		}

		// Erste Serie immer durchgezogen
		if (count > 0) {
			r.setSeriesStroke(0, DiagramStrokeFactory.createStroke());
		}

		/**
		 * Unsere "Strichteile" die zur Verfügung stehen, anzahl beliebig
		 * (mindestens 2, sonst wirds mühsam mit der Unterscheidung;-))
		 */
		final float[] DASH_FRAG = new float[] { 10f, 15f, 20f };

		/**
		 * Wir bilden "Nummern" unsere Zahlen sind die "DASH_FRAG", dazu
		 * berechnen wir hier die maximale länge der Nummer (Alles mit führenden
		 * Nullen, denn es wird enlos widerholt.
		 * 
		 * Bsp: 1 und 11
		 * 
		 * Endlos widerhohlt ist beides 1111111111111111...
		 * 
		 * Dies wird dadruch vermiden
		 */
		int parts = (int) Math.ceil(Math.log(count) / Math.log(DASH_FRAG.length));

		/**
		 * Unserer "Zähler"
		 */
		int index = 0;

		float[] dash = new float[parts];

		for (int i = 1; i < count; i++) {

			int u = index;
			for (int j = 0; j < parts; j++) {
				dash[j] = DASH_FRAG[u % DASH_FRAG.length];
				u /= DASH_FRAG.length;
			}

			r.setSeriesStroke(i, DiagramStrokeFactory.createStroke(2, dash));

			index++;
		}
	}

	public void setFont(Font font) {
		if (font == null) {
			throw new NullPointerException("font == null");
		}

		if (font.equals(lastUsedFont)) {
			return;
		}
		lastUsedFont = font;

		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis axisY = plot.getRangeAxis();
		ValueAxis axisX = plot.getDomainAxis();

		axisY.setLabelFont(font);
		axisX.setLabelFont(font);
		LegendTitle legend = this.chart.getLegend();
		legend.setItemFont(font);

		TextTitle title = getTitle();
		title.setFont(font.deriveFont(Font.BOLD | font.getStyle()).deriveFont((float) font.getSize() * 1.3f));
	}

	public Font getFont() {
		XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis axis = plot.getDomainAxis();

		return axis.getLabelFont();
	}
}
