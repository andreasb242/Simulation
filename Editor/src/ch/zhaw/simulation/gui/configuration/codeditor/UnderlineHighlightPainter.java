package ch.zhaw.simulation.gui.configuration.codeditor;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.View;

import butti.javalibs.errorhandler.Errorhandler;

// Painter for underlined highlights
public class UnderlineHighlightPainter extends LayeredHighlighter.LayerPainter {
	protected Color color; // The color for the underline

	public UnderlineHighlightPainter(Color c) {
		color = c;
	}

	public void paint(Graphics g, int offs0, int offs1, Shape bounds,
			JTextComponent c) {
		// Do nothing: this method will never be called
	}

	public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
			JTextComponent c, View view) {
		g.setColor(color == null ? c.getSelectionColor() : color);

		Rectangle alloc = null;
		if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
			if (bounds instanceof Rectangle) {
				alloc = (Rectangle) bounds;
			} else {
				alloc = bounds.getBounds();
			}
		} else {
			try {
				Shape shape = view.modelToView(offs0, Position.Bias.Forward,
						offs1, Position.Bias.Backward, bounds);
				alloc = (shape instanceof Rectangle) ? (Rectangle) shape
						: shape.getBounds();
			} catch (BadLocationException e) {
				Errorhandler.logError(e);
				return null;
			}
		}

		FontMetrics fm = c.getFontMetrics(c.getFont());
		int baseline = alloc.y + alloc.height - fm.getDescent() + 1;

		for (int x = alloc.x; x < alloc.x + alloc.width; x += 5) {
			g.drawLine(x, baseline + 2, x + 2, baseline);
			g.drawLine(x + 3, baseline, x + 4, baseline + 2);
		}

		return alloc;
	}
}