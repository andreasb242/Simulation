package ch.zhaw.simulation.gui.codeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import butti.javalibs.errorhandler.Errorhandler;

/**
 * Track the movement of the Caret by painting a background line at the current
 * caret position.
 * 
 * source:
 * http://acide-0-8-release-2010-2011.googlecode.com/svn-history/r38/trunk/
 * acide/src/gui/fileEditor/fileEditorPanel/fileEditorTextEditionArea/
 * LinePainter.java
 * 
 * License GPL
 */
public class LineHighlighter extends MouseAdapter implements Highlighter.HighlightPainter, CaretListener {
	private JTextComponent component;

	private Color color;

	private Rectangle lastView = new Rectangle();

	/*
	 * The line color will be calculated automatically by attempting to make the
	 * current selection lighter by a factor of 1.2.
	 * 
	 * @param component text component that requires background line painting
	 */
	public LineHighlighter(JTextComponent component) {
		this(component, null);
		setLighter(component.getSelectionColor());
	}

	/*
	 * Manually control the line color
	 * 
	 * @param component text component that requires background line painting
	 * 
	 * @param color the color of the background line
	 */
	public LineHighlighter(JTextComponent component, Color color) {
		this.component = component;
		setColor(color);

		// Add listeners so we know when to change highlighting

		component.addCaretListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);

		// Turn highlighting on by adding a dummy highlight

		try {
			component.getHighlighter().addHighlight(0, 0, this);
		} catch (BadLocationException e) {
			Errorhandler.logError(e, "addHighlight invalid position");
		}
	}

	/*
	 * You can reset the line color at any time
	 * 
	 * @param color the color of the background line
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/*
	 * Calculate the line color by making the selection color lighter
	 * 
	 * @return the color of the background line
	 */
	public void setLighter(Color color) {
		int red = Math.min(255, (int) (color.getRed() * 1.2));
		int green = Math.min(255, (int) (color.getGreen() * 1.2));
		int blue = Math.min(255, (int) (color.getBlue() * 1.2));
		setColor(new Color(red, green, blue));
	}

	// Paint the background highlight
	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			Rectangle r = c.modelToView(c.getCaretPosition());
			g.setColor(color);
			g.fillRect(0, r.y, c.getWidth(), r.height);

			if (lastView == null) {
				lastView = r;
			}
		} catch (BadLocationException e) {
			Errorhandler.logError(e);
		}
	}

	/*
	 * Caret position has changed, remove the highlight
	 */
	private void resetHighlight() {
		// Use invokeLater to make sure updates to the Document are completed,
		// otherwise Undo processing causes the modelToView method to loop.

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					int offset = component.getCaretPosition();
					Rectangle currentView = component.modelToView(offset);

					// Remove the highlighting from the previously highlighted
					// line

					if (lastView.y != currentView.y) {
						component.repaint(0, lastView.y, component.getWidth(), lastView.height);
						lastView = currentView;
					}
				} catch (BadLocationException e) {
					Errorhandler.logError(e);
				}
			}
		});
	}

	// Implement CaretListener
	@Override
	public void caretUpdate(CaretEvent e) {
		resetHighlight();
	}

	// Implement MouseListener
	@Override
	public void mousePressed(MouseEvent e) {
		resetHighlight();
	}

	// Implement MouseMotionListener
	@Override
	public void mouseDragged(MouseEvent e) {
		resetHighlight();
	}
}
