package ch.zhaw.simulation.diagram.strokeeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class SeriesStrokeColorEditor extends JPanel {
	private static final long serialVersionUID = 1L;

	/** The resourceBundle for the localization. */
	protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.editor.LocalizationBundle");

	private Vector<EditorDataRow> samples = new Vector<EditorDataRow>();

	private Paint diagramBackground;

	public SeriesStrokeColorEditor(JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();

		setLayout(new GridLayout(0, 5, 5, 5));

		diagramBackground = plot.getBackgroundPaint();

		addTitle("Name"); // Name
		addTitle("Vorschau"); // Preview
		addTitle("Dicke"); // Thikness
		addTitle("Linienart"); // Dash / Line style
		addTitle("Farbe"); // Color

		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			XYSeries s = dataset.getSeries(i);
			add(new JLabel(s.getKey().toString()));
			final Paint paint = renderer.lookupSeriesPaint(i);

			final EditorDataRow data = new EditorDataRow(paint, /* TODO */2, null, (Color) diagramBackground);
			samples.add(data);
			data.add(this);

			JButton button = new JButton(localizationResources.getString("Select..."));
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Color c = JColorChooser.showDialog(SeriesStrokeColorEditor.this, "Linienfarbe", (Color) paint);
					if (c != null) {
						data.setPaint(c);
					}

				}
			});
			add(button);
		}
	}

	private void addTitle(String title) {
		JLabel lb = new JLabel(title);
		lb.setFont(lb.getFont().deriveFont(Font.BOLD));
		add(lb);
	}

	public void updateChart(JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

		for (int i = 0; i < samples.size(); i++) {
			EditorDataRow data = samples.get(i);
			renderer.setSeriesPaint(i, data.getPaint());
			renderer.setSeriesStroke(i, data.getStroke());
		}
	}

}
