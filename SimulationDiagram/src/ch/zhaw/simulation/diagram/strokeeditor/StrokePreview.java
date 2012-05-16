package ch.zhaw.simulation.diagram.strokeeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * @author Andreas Butti
 */
public class StrokePreview extends JComponent {
	private static final long serialVersionUID = 1L;

	/**
	 * The paint.
	 */
	private Paint paint;

	/**
	 * The Stroke
	 */
	private StrokData stroke = new StrokData();

	/**
	 * Shows this stroke instead of the configured
	 */
	private BasicStroke strokeOverwrite = null;

	/**
	 * The background of the diagram
	 */
	private Color diagramBackground;

	/**
	 * Standard constructor - builds a paint sample.
	 * 
	 * @param paint
	 *            the paint to display.
	 */
	public StrokePreview(Paint paint, Color diagramBackground) {
		this.paint = paint;
		this.diagramBackground = diagramBackground;
	}

	/**
	 * Returns the current Paint object being displayed in the panel.
	 * 
	 * @return the paint.
	 */
	public Paint getPaint() {
		return this.paint;
	}

	public BasicStroke getStroke() {
		return stroke.getStroke();
	}

	/**
	 * Sets the Paint object being displayed in the panel.
	 * 
	 * @param paint
	 *            the paint.
	 */
	public void setPaint(Paint paint) {
		this.paint = paint;
		repaint();
	}

	public void setThikness(float thikness) {
		this.stroke.setThikness(thikness);
		repaint();
	}

	public float getThikness() {
		return this.stroke.getThikness();
	}

	public float[] getDash() {
		return this.stroke.getDash();
	}

	public void setDash(float[] dash) {
		this.stroke.setDash(dash);
		repaint();
	}

	public void setStroke(BasicStroke stroke) {
		this.strokeOverwrite = stroke;
		repaint();
	}

	/**
	 * Fills the component with the current Paint.
	 * 
	 * @param g
	 *            the graphics device.
	 */
	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;
		final Dimension size = getSize();
		final Insets insets = getInsets();
		final double xx = insets.left;
		final double yy = insets.top;
		final double ww = size.getWidth() - insets.left - insets.right - 1;
		final double hh = size.getHeight() - insets.top - insets.bottom - 1;
		final Rectangle2D area = new Rectangle2D.Double(xx, yy, ww, hh);

		Stroke defaultStroke = g2.getStroke();

		if (diagramBackground != null) {
			g2.setPaint(diagramBackground);
			g2.fill(area);
		}

		if (this.strokeOverwrite != null) {
			g2.setStroke(this.strokeOverwrite);
		} else {
			g2.setStroke(stroke.getStroke());
		}

		g2.setPaint(this.paint);

		final double y = yy + hh / 2;
		Line2D.Double line = new Line2D.Double(xx, y, xx + ww, y);
		g2.draw(line);

		g2.setStroke(defaultStroke);
		g2.setPaint(Color.BLACK);
		g2.draw(area);

	}
}