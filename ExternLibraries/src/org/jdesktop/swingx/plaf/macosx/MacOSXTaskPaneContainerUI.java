/*
 * $Id: BasicTaskPaneContainerUI.java 3475 2009-08-28 08:30:47Z kleopatra $
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
package org.jdesktop.swingx.plaf.macosx;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.plaf.basic.BasicTaskPaneContainerUI;

import com.explodingpixels.widgets.WindowUtils;

/**
 * Base implementation of the <code>JXTaskPaneContainer</code> UI.
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 * @author Karl Schaefer
 */
public class MacOSXTaskPaneContainerUI extends BasicTaskPaneContainerUI {
	private WindowFocusListener focusListener = new WindowFocusListener() {
		public void windowGainedFocus(WindowEvent e) {
			setColor(UIManager.getColor("TaskPaneContainer.background"));
		}

		private void setColor(Color color) {
			if (color == null) {
				return;
			}
			taskPane.setBackground(color);
		}

		public void windowLostFocus(WindowEvent e) {
			setColor(UIManager.getColor("TaskPaneContainer.background.inactive"));
		}
	};

	private AncestorListener ancestorListener;

	/**
	 * Returns a new instance of BasicTaskPaneContainerUI.
	 * BasicTaskPaneContainerUI delegates are allocated one per
	 * JXTaskPaneContainer.
	 * 
	 * @return A new TaskPaneContainerUI implementation for the Basic look and
	 *         feel.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new MacOSXTaskPaneContainerUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		this.ancestorListener = WindowUtils.createAncestorListener(c, focusListener);
		c.addAncestorListener(ancestorListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		c.removeAncestorListener(this.ancestorListener);
		super.uninstallUI(c);
	}
}
