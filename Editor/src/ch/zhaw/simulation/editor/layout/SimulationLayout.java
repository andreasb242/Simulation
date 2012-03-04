package ch.zhaw.simulation.editor.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import ch.zhaw.simulation.editor.elements.AbstractDataView;
import ch.zhaw.simulation.model.element.AbstractSimulationData;

public class SimulationLayout implements LayoutManager {

	@Override
	public void addLayoutComponent(String name, Component comp) {
		layoutComponent(comp.getParent(), null);
	}

	@Override
	public void layoutContainer(Container parent) {
		for (Component c : parent.getComponents()) {
			layoutComponent(c, parent);
		}
	}

	private void layoutComponent(Component c, Container parent) {
		if (c instanceof AbstractDataView<?>) {
			AbstractDataView<?> e = (AbstractDataView<?>) c;
			AbstractSimulationData data = e.getData();

			Dimension size = e.getPreferredSize();

			c.setBounds(data.getX(), data.getY(), (int) size.getWidth(), (int) size.getHeight());
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		int w = 0;
		int h = 0;

		for (Component c : parent.getComponents()) {
			if (c instanceof AbstractDataView<?>) {
				AbstractDataView<?> e = (AbstractDataView<?>) c;

				Dimension p = e.getPreferredSize();
				AbstractSimulationData d = e.getData();

				w = Math.max(w, d.getX() + (int) p.getWidth());
				h = Math.max(h, d.getY() + (int) p.getHeight());
			} else {
				Rectangle b = c.getBounds();

				w = Math.max(w, (int) (b.getX() + b.getWidth()));
				h = Math.max(h, (int) (b.getY() + b.getHeight()));
			}
		}

		return new Dimension(w, h);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension d = minimumLayoutSize(parent);
		return new Dimension((int) d.getWidth() + 10, (int) d.getHeight() + 10);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}
}
