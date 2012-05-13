package ch.zhaw.simulation.diagram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.ui.RectangleInsets;

/**
 * (AB)² Chart theme
 */
public class SimulationDiagramTheme extends StandardChartTheme {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates and returns the default '(AB)²' chart theme.
	 * 
	 * @return A chart theme.
	 */
	public static ChartTheme createJFreeTheme() {
		return new SimulationDiagramTheme("(AB)²");
	}

	/**
	 * Creates a new default instance.
	 * 
	 * @param name
	 *            the name of the theme (<code>null</code> not permitted).
	 */
	public SimulationDiagramTheme(String name) {
		this(name, false);
	}

	/**
	 * Creates a new default instance.
	 * 
	 * @param name
	 *            the name of the theme (<code>null</code> not permitted).
	 * @param shadow
	 *            a flag that controls whether a shadow generator is included.
	 * 
	 * @since 1.0.14
	 */
	public SimulationDiagramTheme(String name, boolean shadow) {
		super(name, shadow);

		setExtraLargeFont(new Font("Tahoma", Font.BOLD, 20));
		setLargeFont(new Font("Tahoma", Font.BOLD, 14));
		setRegularFont(new Font("Tahoma", Font.PLAIN, 12));
		setSmallFont(new Font("Tahoma", Font.PLAIN, 10));
		setTitlePaint(Color.BLACK);
		setSubtitlePaint(Color.BLACK);
		setLegendBackgroundPaint(Color.WHITE);
		setLegendItemPaint(Color.DARK_GRAY);
		setChartBackgroundPaint(Color.WHITE);

		Stroke[] strokePaint = new Stroke[] { DiagramStrokeFactory.createStroke() };

		setDrawingSupplier(new DefaultDrawingSupplier(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, strokePaint, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		setPlotBackgroundPaint(Color.WHITE);
		setPlotOutlinePaint(Color.BLACK);
		setLabelLinkPaint(Color.BLACK);
		setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);
		setAxisOffset(new RectangleInsets(4, 4, 4, 4));
		setDomainGridlinePaint(Color.DARK_GRAY);
		setRangeGridlinePaint(Color.DARK_GRAY);
		setBaselinePaint(Color.BLACK);
		setCrosshairPaint(Color.BLUE);
		setAxisLabelPaint(Color.DARK_GRAY);
		setTickLabelPaint(Color.DARK_GRAY);
		setBarPainter(new GradientBarPainter());
		setXYBarPainter(new GradientXYBarPainter());
		setShadowVisible(false);
		setShadowPaint(Color.GRAY);
		setItemLabelPaint(Color.BLACK);
		setThermometerPaint(Color.WHITE);
		setWallPaint(BarRenderer3D.DEFAULT_WALL_PAINT);
		setErrorIndicatorPaint(Color.BLACK);
	}

}
