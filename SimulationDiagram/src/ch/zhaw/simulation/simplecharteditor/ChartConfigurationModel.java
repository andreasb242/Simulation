package ch.zhaw.simulation.simplecharteditor;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;

public class ChartConfigurationModel {
	private JFreeChart chart;

	public ChartConfigurationModel(JFreeChart chart) {
		this.chart = chart;
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

		TextTitle title = this.chart.getTitle();

		if (title == null) {
			title = new TextTitle();
			this.chart.setTitle(title);
		}

		title.setVisible(!text.isEmpty());
		title.setText(text);
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
}
