package ch.zhaw.simulation.sysintegration.mac;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class MacToolbarLayoutManager implements LayoutManager {
	private static final int PADDING = 10;

	public MacToolbarLayoutManager() {
	}

	public void addLayoutComponent(String name, Component c) {
	}

	public void layoutContainer(Container parent) {
		int usedSpace = 0;
		int seperaterCount = 0;

		for (Component m : parent.getComponents()) {
			if (m instanceof MacToolbarSeparator) {
				seperaterCount++;
			}

			usedSpace += m.getPreferredSize().width + PADDING;
		}

		usedSpace += PADDING;

		int seperatorSpace = 0;
		if (seperaterCount != 0) {
			seperatorSpace = (parent.getWidth() - usedSpace) / seperaterCount;
		}

		Insets insets = parent.getInsets();
		Dimension size = parent.getSize();
		int height = size.height - insets.top - insets.bottom - 2 * PADDING;
		int width = insets.left;
		for (Component m : parent.getComponents()) {
			if (m.isVisible()) {
				m.setBounds(width + PADDING, insets.top + PADDING, m.getPreferredSize().width, height);
				width += m.getSize().width + PADDING;
				if (m instanceof MacToolbarSeparator) {
					width += seperatorSpace;
				}
			}
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	public Dimension preferredLayoutSize(Container parent) {
		Insets insets = parent.getInsets();
		Dimension pref = new Dimension(0, 0);
		for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
			Component m = parent.getComponent(i);
			if (m.isVisible()) {
				Dimension componentPreferredSize = parent.getComponent(i).getPreferredSize();
				pref.height = Math.max(pref.height, componentPreferredSize.height);
				pref.width += componentPreferredSize.width + PADDING;
			}
		}
		pref.width += insets.left + insets.right;
		pref.height += insets.top + insets.bottom + 2 * PADDING;
		return pref;
	}

	public void removeLayoutComponent(Component c) {
	}
}