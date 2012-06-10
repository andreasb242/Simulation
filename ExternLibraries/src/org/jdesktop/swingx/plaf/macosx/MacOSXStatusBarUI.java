package org.jdesktop.swingx.plaf.macosx;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI;

import com.explodingpixels.widgets.WindowUtils;

/**
 * 
 * @author Andreas Butti, based on SwingX Version and
 *         http://exploding-pixels.com
 *         /google_code/javadoc/com/explodingpixels/macwidgets/BottomBar.html
 */
public class MacOSXStatusBarUI extends BasicStatusBarUI {
	private static final Color ACTIVE_TOP_COLOR = new Color(0xcbcbcb);
	private static final Color ACTIVE_BOTTOM_COLOR = new Color(0xa7a7a7);
	private static final Color INACTIVE_COLOR = new Color(0xeaeaea);

	/**
	 * Returns an instance of the UI delegate for the specified component. Each
	 * subclass must provide its own static <code>createUI</code> method that
	 * returns an instance of that UI delegate subclass. If the UI delegate
	 * subclass is stateless, it may return an instance that is shared by
	 * multiple components. If the UI delegate is stateful, then it should
	 * return a new instance per component. The default implementation of this
	 * method throws an error, as it should never be invoked.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new MacOSXStatusBarUI();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void installDefaults(JXStatusBar sb) {
		super.installDefaults(sb);

		WindowUtils.installJComponentRepainterOnWindowFocusChanged(sb);
	}

	@Override
	protected void paintBackground(Graphics2D g, JXStatusBar bar) {
		int w = bar.getWidth();
		int h = bar.getHeight();

		if (WindowUtils.isParentWindowFocused(bar)) {
			g.setPaint(new GradientPaint(0, 0, ACTIVE_TOP_COLOR, 0, h, ACTIVE_BOTTOM_COLOR));
		} else {
			g.setPaint(INACTIVE_COLOR);
		}

		g.fillRect(0, 0, w, h);
	}
}
