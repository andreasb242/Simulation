/*
 * $Id: GlossyTaskPaneUI.java 3448 2009-08-19 08:12:23Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.plaf.misc;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

import com.explodingpixels.widgets.WindowUtils;

/**
 * Paints the JXTaskPane with a gradient in the title bar.
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class GlossyTaskPaneUI extends BasicTaskPaneUI {
	private WindowFocusListener focusListener = new WindowFocusListener() {
		public void windowGainedFocus(WindowEvent e) {
			setColor(UIManager.getColor("TaskPaneContainer.background").getRGB());
		}

		private void setColor(int rgb) {
			group.getContentPane().setBackground(new Color(rgb));
		}

		public void windowLostFocus(WindowEvent e) {
			setColor(UIManager.getColor("TaskPaneContainer.background.inactive").getRGB());
		}
	};
	private AncestorListener ancestorListener;

	public static ComponentUI createUI(JComponent c) {
		return new GlossyTaskPaneUI();
	}

	@Override
	protected Border createPaneBorder() {
		return new GlossyPaneBorder();
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		this.ancestorListener = WindowUtils.createAncestorListener(group, focusListener);
		group.addAncestorListener(ancestorListener);

	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
	}

	@Override
	protected void uninstallListeners() {
		group.removeAncestorListener(ancestorListener);

		super.uninstallListeners();
	}

	/**
	 * Overriden to paint the background of the component but keeping the
	 * rounded corners.
	 */
	@Override
	public void update(Graphics g, JComponent c) {
		if (c.isOpaque()) {
			g.setColor(c.getParent().getBackground());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
			g.setColor(c.getBackground());
			g.fillRect(0, getRoundHeight(), c.getWidth(), c.getHeight() - getRoundHeight());
		}
		paint(g, c);
	}

	/**
	 * The border of the taskpane group paints the "text", the "icon", the
	 * "expanded" status and the "special" type.
	 * 
	 */
	class GlossyPaneBorder extends PaneBorder {

		@Override
		protected void paintTitleBackground(JXTaskPane group, Graphics g) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (group.isSpecial()) {
				g.setColor(specialTitleBackground);
				g.fillRoundRect(0, 0, group.getWidth(), getRoundHeight() * 2, getRoundHeight(), getRoundHeight());
				g.fillRect(0, getRoundHeight(), group.getWidth(), getTitleHeight(group) - getRoundHeight());
			} else {
				Paint oldPaint = ((Graphics2D) g).getPaint();
				GradientPaint gradient = new GradientPaint(0f, 0f, titleBackgroundGradientStart, 0f, getTitleHeight(group), titleBackgroundGradientEnd);

				((Graphics2D) g).setPaint(gradient);

				g.fillRoundRect(0, 0, group.getWidth(), getRoundHeight() * 2, getRoundHeight(), getRoundHeight());
				g.fillRect(0, getRoundHeight(), group.getWidth(), getTitleHeight(group) - getRoundHeight());
				((Graphics2D) g).setPaint(oldPaint);
			}

			g.setColor(borderColor);
			g.drawRoundRect(0, 0, group.getWidth() - 1, getTitleHeight(group) + getRoundHeight(), getRoundHeight(), getRoundHeight());
			g.drawLine(0, getTitleHeight(group) - 1, group.getWidth(), getTitleHeight(group) - 1);
		}

		@Override
		protected void paintExpandedControls(JXTaskPane group, Graphics g, int x, int y, int width, int height) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			paintOvalAroundControls(group, g, x, y, width, height);
			g.setColor(getPaintColor(group));
			paintChevronControls(group, g, x, y, width, height);

			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		@Override
		protected void paintFocus(Graphics g, Color paintColor, int x, int y, int width, int height) {
		}

		@Override
		protected boolean isMouseOverBorder() {
			return true;
		}

	}

}
