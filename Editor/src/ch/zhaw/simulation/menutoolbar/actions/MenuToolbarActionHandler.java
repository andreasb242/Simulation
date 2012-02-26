package ch.zhaw.simulation.menutoolbar.actions;

import java.util.Vector;

import ch.zhaw.simulation.menu.MenuActionListener;

public class MenuToolbarActionHandler {
	private Vector<MenuActionListener> listeners = new Vector<MenuActionListener>();

	public void addListener(MenuActionListener l) {
		listeners.add(l);
	}

	public void removeListener(MenuActionListener l) {
		listeners.remove(l);
	}

	public void fireMenuActionPerformed(MenuToolbarAction a) {
		for (MenuActionListener l : listeners) {
			l.menuActionPerformed(a);
		}
	}
}
