package ch.zhaw.simulation.frame.sidebar;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * The sidebar of our editor
 * 
 * @author Andreas Butti
 */
public class FrameSidebar extends JXTaskPaneContainer {
	private static final long serialVersionUID = 1L;

	public FrameSidebar() {
	}

	@Override
	public Dimension getMinimumSize() {
		int h = super.getMinimumSize().height;
		return new Dimension(0, h);
	}

	public void reorderTaskPanes() {
		Vector<Component> comps = new Vector<Component>();
		for (Component c : getComponents()) {
			comps.add(c);
		}

		Collections.sort(comps, new Comparator<Component>() {

			@Override
			public int compare(Component o1, Component o2) {
				if (o1 instanceof SidebarPosition && o2 instanceof SidebarPosition) {
					return ((SidebarPosition) o1).getSidebarPosition() - ((SidebarPosition) o2).getSidebarPosition();
				}
				return 0;
			}
		});

		for (int i = 0; i < comps.size(); i++) {
			Component c = comps.get(i);
			setComponentZOrder(c, i);
		}
	}

	@Override
	public void add(JXTaskPane group) {
		if (!(group instanceof SidebarPosition)) {
			System.err.println("FrameSidebar::add " + group.getClass() + " should implement SidebarPosition");
		}

		super.add(group);

		reorderTaskPanes();
	}

}
