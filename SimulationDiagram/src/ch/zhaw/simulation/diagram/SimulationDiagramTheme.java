package ch.zhaw.simulation.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Stroke;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleInsets;

import butti.javalibs.config.Config;

/**
 * (AB)² Chart theme
 */
public class SimulationDiagramTheme extends StandardChartTheme {

	private static final long serialVersionUID = 1L;

	private BasicStroke baseStroke;

	public static final Color DEFAULT_OUTLINE_PAINT = Color.LIGHT_GRAY;
	public static final BasicStroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
	public static final Font DEFAULT_TITLE_FONT;
	public static final Font DEFAULT_FONT_AXIS_TICK_LEGEND;
	public static final Color DEFAULT_AXIS_LABEL_PAINT = Color.DARK_GRAY;
	public static final Color DEFAULT_TICK_LABEL_PAINT = Color.DARK_GRAY;
	public static final String DEFAULT_FONT_NAME;

	static {
		String[] fonts = Config.getArray("defaultsFonts");
		String fontName = "Sans";

		// load available font names
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();

		outFontLoop: for (String font : fonts) {
			for (String predefinedFont : fontNames) {
				if (predefinedFont.equalsIgnoreCase(font)) {
					fontName = predefinedFont;
					break outFontLoop;
				}
			}
		}

		System.out.println("Using font «" + fontName + "» for Diagram as default font");

		DEFAULT_FONT_NAME = fontName;
		DEFAULT_TITLE_FONT = new Font(fontName, Font.BOLD, 12);
		DEFAULT_FONT_AXIS_TICK_LEGEND = new Font(fontName, 0, 10);
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

		setExtraLargeFont(new Font(DEFAULT_FONT_NAME, Font.BOLD, 20));
		setLargeFont(new Font(DEFAULT_FONT_NAME, Font.BOLD, 14));
		setRegularFont(new Font(DEFAULT_FONT_NAME, Font.PLAIN, 12));
		setSmallFont(new Font(DEFAULT_FONT_NAME, Font.PLAIN, 10));
		setTitlePaint(Color.BLACK);
		setSubtitlePaint(Color.BLACK);
		setLegendBackgroundPaint(Color.WHITE);
		setLegendItemPaint(Color.DARK_GRAY);
		setChartBackgroundPaint(Color.WHITE);

		this.baseStroke = DiagramStrokeFactory.createStroke();

		Stroke[] strokePaint = new Stroke[] { this.baseStroke };

		setDrawingSupplier(new DefaultDrawingSupplier(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
				new Color[] { DEFAULT_OUTLINE_PAINT }, strokePaint, new Stroke[] { DEFAULT_OUTLINE_STROKE }, DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
		setPlotBackgroundPaint(Color.WHITE);
		setPlotOutlinePaint(Color.BLACK);
		setLabelLinkPaint(Color.BLACK);
		setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);
		setAxisOffset(new RectangleInsets(4, 4, 4, 4));
		setDomainGridlinePaint(Color.DARK_GRAY);
		setRangeGridlinePaint(Color.DARK_GRAY);
		setBaselinePaint(Color.BLACK);
		setCrosshairPaint(Color.BLUE);
		setAxisLabelPaint(DEFAULT_AXIS_LABEL_PAINT);
		setTickLabelPaint(DEFAULT_TICK_LABEL_PAINT);
		setBarPainter(new GradientBarPainter());
		setXYBarPainter(new GradientXYBarPainter());
		setShadowVisible(false);
		setShadowPaint(Color.GRAY);
		setItemLabelPaint(Color.BLACK);
		setThermometerPaint(Color.WHITE);
		setWallPaint(BarRenderer3D.DEFAULT_WALL_PAINT);
		setErrorIndicatorPaint(Color.BLACK);
	}

	@Override
	public void apply(JFreeChart chart) {
		super.apply(chart);

		LegendTitle legend = chart.getLegend();
		if (legend != null) {
			legend.setItemFont(DEFAULT_FONT_AXIS_TICK_LEGEND);
		}
	}

}
