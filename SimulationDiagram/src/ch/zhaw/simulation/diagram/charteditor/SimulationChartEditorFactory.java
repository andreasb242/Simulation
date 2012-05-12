package ch.zhaw.simulation.diagram.charteditor;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorFactory;

public class SimulationChartEditorFactory implements ChartEditorFactory {

	@Override
	public ChartEditor createEditor(JFreeChart chart) {
		// TODO zeichnung glätten!?!?
		
		return new SimulationChartEditor(chart);
	}

}
